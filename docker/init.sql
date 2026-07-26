--postgres sql 유저생성 및 권한부여, 비밀번호 설정
CREATE ROLE dev1 WITH CREATEDB LOGIN PASSWORD 'nakama-dev1';

--postgres database 생성
CREATE DATABASE dev1_db OWNER dev1;