CREATE TABLE user (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      full_name VARCHAR(255) NOT NULL,
                      email VARCHAR(255) UNIQUE NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      role VARCHAR(50) NOT NULL, -- Will hold values like 'ADMIN', 'MEMBER', 'LIBRARIAN'
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE author (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255),
                        biography TEXT
);


CREATE TABLE announcement (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              title VARCHAR(255),
                              content VARCHAR(1000),
                              publish_date DATETIME
);

CREATE TABLE book (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      isbn VARCHAR(100) NOT NULL UNIQUE,
                      quantity INT NOT NULL,
                      author_id BIGINT,
                      category_id BIGINT,
                      publisher_id BIGINT,
                      library_branch_id BIGINT,
                      CONSTRAINT fk_book_author FOREIGN KEY (author_id) REFERENCES author(id),
                      CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES category(id),
                      CONSTRAINT fk_book_publisher FOREIGN KEY (publisher_id) REFERENCES publisher(id),
                      CONSTRAINT fk_book_shelf FOREIGN KEY (shelf_id) REFERENCES shelf(id),
                      CONSTRAINT fk_book_library_branch FOREIGN KEY (library_branch_id) REFERENCES library_branch(id)
);


CREATE TABLE category (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL UNIQUE,
                          description VARCHAR(500)
);


CREATE TABLE event (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description VARCHAR(500),
                       date DATE
);


CREATE TABLE fine (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      amount DECIMAL(10,2) NOT NULL,
                      issued_date DATE NOT NULL,
                      paid BOOLEAN,
                      user_id BIGINT NOT NULL,
                      CONSTRAINT fk_fine_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE inventory (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           quantity_available INT NOT NULL,
                           book_id BIGINT NOT NULL,
                           CONSTRAINT fk_inventory_book FOREIGN KEY (book_id) REFERENCES book(id)
);


CREATE TABLE library_branch (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                branch_name VARCHAR(255) NOT NULL,
                                address VARCHAR(255) NOT NULL
);

CREATE TABLE library_card (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              issued_date DATE NOT NULL,
                              user_id BIGINT UNIQUE,
                              CONSTRAINT fk_library_card_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE loan_history (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              loan_date DATE,
                              return_date DATE,
                              user_id BIGINT,
                              book_id BIGINT,
                              CONSTRAINT fk_loan_history_user FOREIGN KEY (user_id) REFERENCES user(id),
                              CONSTRAINT fk_loan_history_book FOREIGN KEY (book_id) REFERENCES book(id)
);

CREATE TABLE membership (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            start_date DATE NOT NULL,
                            end_date DATE NOT NULL,
                            status VARCHAR(20) NOT NULL,
                            user_id BIGINT UNIQUE,
                            CONSTRAINT fk_membership_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE notification (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              message VARCHAR(255) NOT NULL,
                              sent_at DATETIME NOT NULL,
                              user_id BIGINT,
                              CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES user(id)
);
CREATE TABLE publisher (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           address VARCHAR(255)
);
CREATE TABLE reservation (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             book_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             loan_date DATE NOT NULL,
                             due_date DATE NOT NULL,
                             returned BOOLEAN DEFAULT FALSE,
                             status VARCHAR(20) NOT NULL,
                             CONSTRAINT fk_reservation_book FOREIGN KEY (book_id) REFERENCES book(id),
                             CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES user(id)
);


CREATE TABLE review (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        comment TEXT,
                        rating INT NOT NULL,
                        user_id BIGINT,
                        book_id BIGINT,
                        CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES user(id),
                        CONSTRAINT fk_review_book FOREIGN KEY (book_id) REFERENCES book(id)
);

CREATE TABLE shelf (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       code VARCHAR(50) NOT NULL UNIQUE,
                       location_description VARCHAR(255)
);










