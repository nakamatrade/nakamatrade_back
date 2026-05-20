package menuservice.global.swagger;

import menuservice.global.exception.ErrorCode;
import menuservice.global.exception.ErrorResponse;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class SwaggerErrorResponseCustomizer {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String ERROR_RESPONSE_GROUP_CODE = "40X";

    @Bean
    OperationCustomizer apiErrorResponseCustomizer() {
        return (operation, handlerMethod) -> {
            ApiErrorResponse annotation = handlerMethod.getMethodAnnotation(ApiErrorResponse.class);

            if (Objects.nonNull(annotation)) {
                addErrorExamplesToOperation(operation, annotation.value(), annotation.description());
            }

            return operation;
        };
    }

    private void addErrorExamplesToOperation(Operation operation, ErrorCode[] errorCodes, String description) {
        ApiResponses responses = operation.getResponses();
        Content content = new Content();
        MediaType mediaType = new MediaType();

        for (ErrorCode errorCode : errorCodes) {
            mediaType.addExamples(errorCode.name(), createErrorExample(errorCode));
        }

        content.addMediaType(CONTENT_TYPE_JSON, mediaType);
        responses.addApiResponse(ERROR_RESPONSE_GROUP_CODE, new ApiResponse().description(description).content(content));
    }

    private Example createErrorExample(ErrorCode errorCode) {
        Example example = new Example();
        example.setValue(new ErrorResponse(errorCode.name(), errorCode.getMessage()));
        return example;
    }
}
