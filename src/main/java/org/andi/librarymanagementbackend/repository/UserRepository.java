// src/main/java/org/andi/librarymanagementbackend/repository/UserRepository.java
package org.andi.librarymanagementbackend.repository;

import org.andi.librarymanagementbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    /**
     * Change the role discriminator and wipe _all_ subtype columns in one go.
     */
    @Modifying
    @Transactional
    @Query(
            value = """
        UPDATE `user`
           SET role              = :newRole,
               membership_id     = NULL,
               admin_code        = NULL,
               department        = NULL,
               employee_number   = NULL
         WHERE id = :id
      """,
            nativeQuery = true
    )
    void updateRole(
            @Param("id") Long id,
            @Param("newRole") String newRole
    );

    /**
     * Populate just the two librarian columns.
     */
    @Modifying
    @Transactional
    @Query(
            value = """
        UPDATE `user`
           SET department      = :dept,
               employee_number = :emp
         WHERE id = :id
      """,
            nativeQuery = true
    )
    void updateLibrarianFields(
            @Param("id") Long id,
            @Param("dept") String department,
            @Param("emp") String employeeNumber
    );

    /**
     * Populate just the one admin column.
     */
    @Modifying
    @Transactional
    @Query(
            value = """
        UPDATE `user`
           SET admin_code = :code
         WHERE id = :id
      """,
            nativeQuery = true
    )
    void updateAdminCode(
            @Param("id") Long id,
            @Param("code") String adminCode
    );

    /**
     * Populate just the one member column.
     */
    @Modifying
    @Transactional
    @Query(
            value = """
        UPDATE `user`
           SET membership_id = :mid
         WHERE id = :id
      """,
            nativeQuery = true
    )
    void updateMemberFields(
            @Param("id") Long id,
            @Param("mid") String membershipId
    );
}
