-- 스터디 모집 · 스키마 생성
--
-- 표는 프레임워크가 생성함. 개발에서는 update, 운영에서는 validate 로 둠.

CREATE DATABASE IF NOT EXISTS study_app
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'study'@'localhost' IDENTIFIED BY 'Study!1234';
GRANT ALL PRIVILEGES ON study_app.* TO 'study'@'localhost';
FLUSH PRIVILEGES;
