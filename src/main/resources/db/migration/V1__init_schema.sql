CREATE TABLE user (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      full_name VARCHAR(255),
                      email VARCHAR(255) UNIQUE,
                      password VARCHAR(255),
                      role VARCHAR(20)
);

CREATE TABLE book (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      title VARCHAR(255),
                      author VARCHAR(255),
                      isbn VARCHAR(100),
                      category VARCHAR(100),
                      quantity INT
);

CREATE TABLE reservation (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             book_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             loan_date DATE,
                             due_date DATE,
                             returned BOOLEAN

                             CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES book(id),
                             CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user(id)
);
