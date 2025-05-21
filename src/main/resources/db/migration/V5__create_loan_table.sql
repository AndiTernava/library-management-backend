

CREATE TABLE loan (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      book_id BIGINT NOT NULL,
                      user_id BIGINT NOT NULL,
                      checkout_date DATE NOT NULL,
                      due_date DATE NOT NULL,
                      return_date DATE,
                      status VARCHAR(20) NOT NULL,
                      tenant_id VARCHAR(100) NOT NULL,
                      CONSTRAINT fk_loan_book FOREIGN KEY (book_id) REFERENCES book(id),
                      CONSTRAINT fk_loan_user FOREIGN KEY (user_id) REFERENCES user(id)
);

