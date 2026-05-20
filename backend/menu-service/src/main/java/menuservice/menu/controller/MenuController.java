package menuservice.menu.controller;

import lombok.RequiredArgsConstructor;
import menuservice.global.common.Result;
import menuservice.menu.domain.Menu;
import menuservice.menu.dto.MenuRequest;
import menuservice.menu.dto.MenuResponse;
import menuservice.menu.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/menu") 
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public Result<MenuResponse> createMenu(@Valid @RequestBody MenuRequest requestDto) {
        MenuResponse response = menuService.createMenu(requestDto);

        return Result.success(response);
    }
    
    @PutMapping("/{menuId}")
    public Result<Object> updateMenu(@PathVariable("menuId") Long menuId, @RequestBody MenuRequest request) {
        menuService.updateMenu(menuId, request);

        return Result.noContent();
    }

    @GetMapping
    public Result<List<MenuResponse>> getAllMenus() {

        return Result.success(menuService.getAllMenus());
    }
}