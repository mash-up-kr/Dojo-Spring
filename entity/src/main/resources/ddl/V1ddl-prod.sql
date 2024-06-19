CREATE TABLE dojo_prod.question
(
    created_at DATETIME(6) NOT NULL,
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    updated_at DATETIME(6) NOT NULL,
    content    VARCHAR(255) NOT NULL,
    target     ENUM('EITHER','FRIEND','STRANGER') NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE dojo_prod.profile
(
    is_deleted       BIT          NOT NULL,
    created_at       DATETIME(6) NOT NULL,
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    member_id        BIGINT,
    updated_at       DATETIME(6) NOT NULL,
    created_by       VARCHAR(255),
    image_url        VARCHAR(255) NOT NULL,
    last_modified_by VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE dojo_prod.pick
(
    created_at     DATETIME(6) NOT NULL,
    from_member_id BIGINT,
    id             BIGINT NOT NULL AUTO_INCREMENT,
    question_id    BIGINT,
    to_member_id   BIGINT,
    updated_at     DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE dojo_prod.notification
(
    send_status BIT          NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    updated_at  DATETIME(6) NOT NULL,
    content     VARCHAR(255) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    topic       VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE dojo_prod.member
(
    generation INT          NOT NULL,
    point      INT          NOT NULL,
    created_at DATETIME(6) NOT NULL,
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    updated_at DATETIME(6) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    gender     ENUM('FEMALE','MALE') NOT NULL,
    part       ENUM('ANDROID','IOS','PRODUCT_DESIGN','SPRING','WEB') NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;
