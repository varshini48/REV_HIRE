package com.rev_hire.client_layer;

import com.rev_hire.controller.UserAccountController;
import com.rev_hire.model.UserAccount;
import com.rev_hire.presentation_layer.EmployerMain;
import com.rev_hire.presentation_layer.JobSeekerMain;
import com.rev_hire.util.ValidationUtil;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static final String ROLE_EMPLOYER = "EMPLOYER";
    private static final String ROLE_JOB_SEEKER = "JOB_SEEKER";

    public static void main(String[] args) {

        logger.info("Application started");

        Scanner sc = new Scanner(System.in);
        UserAccountController controller = new UserAccountController();

        while (true) {
            System.out.println("""
                    =================================
                         WELCOME TO REVHIRE
                    =================================
                    1. Login
                    2. Register
                    3. Exit
                    """);

            System.out.print("Enter choice: ");
            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                logger.warning("Invalid menu input");
                System.out.println("Please enter a valid number");
                continue;
            }

            switch (choice) {
                case 1 -> loginFlow(sc, controller);
                case 2 -> registerFlow(sc, controller);
                case 3 -> {
                    logger.info("Application closed by user");
                    System.out.println("Thank you for using RevHire!");
                    sc.close();
                    System.exit(0);
                }
                default -> {
                    logger.warning("Invalid menu option selected: " + choice);
                    System.out.println("Invalid option. Try again.");
                }
            }
        }
    }

    // ================= LOGIN FLOW =================
    private static void loginFlow(Scanner sc, UserAccountController controller) {

        logger.info("Login flow started");

        System.out.println("\n==== LOGIN ====");

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        System.out.print("Role (JOB_SEEKER / EMPLOYER): ");
        String role = sc.nextLine().trim().toUpperCase();

        if (email.isEmpty() || password.isEmpty()) {
            logger.warning("Login attempt with empty email or password");
            System.out.println("Email and Password cannot be empty");
            return;
        }

        if (!role.equals(ROLE_JOB_SEEKER) && !role.equals(ROLE_EMPLOYER)) {
            logger.warning("Invalid role entered during login: " + role);
            System.out.println("Invalid role selected");
            return;
        }

        logger.info("Login attempt for email: " + email + " with role: " + role);

        UserAccount user = controller.login(email, password, role);

        if (user == null) {
            logger.warning("Invalid login credentials for email: " + email);
            System.out.println("Invalid credentials");

            System.out.print("Forgot Password? (Y/N) or Register? (R): ");
            String choice = sc.nextLine().trim().toUpperCase();

            if (choice.equals("Y")) {
                forgotPasswordFlow(sc, controller);
            } else if (choice.equals("R")) {
                registerFlow(sc, controller);
            }
            return;
        }

        logger.info("Login successful for email: " + email);

        System.out.println("Login successful!");

        if (ROLE_EMPLOYER.equals(user.getRole())) {
            logger.info("Redirecting to Employer dashboard for user ID: " + user.getId());
            EmployerMain.start(sc, user.getId());
        } else {
            logger.info("Redirecting to Job Seeker dashboard for user ID: " + user.getId());
            JobSeekerMain.start(sc, user.getId());
        }
    }

    // ================= REGISTER FLOW =================
    private static void registerFlow(Scanner sc, UserAccountController controller) {

        logger.info("Registration flow started");

        try {
            System.out.println("\n==== REGISTER ====");

            UserAccount user = new UserAccount();

            System.out.print("Email: ");
            String email = sc.nextLine().trim();

            System.out.print("Password: ");
            String password = sc.nextLine().trim();

            System.out.print("Confirm Password: ");
            String confirm = sc.nextLine().trim();

            System.out.print("Role (JOB_SEEKER / EMPLOYER): ");
            String role = sc.nextLine().trim().toUpperCase();

            if (!ValidationUtil.isValidEmail(email)) {
                logger.warning("Invalid email format during registration: " + email);
                System.out.println("Invalid email format");
                return;
            }

            if (!ValidationUtil.isValidPassword(password)) {
                logger.warning("Weak password attempt during registration for email: " + email);
                System.out.println("Weak password");
                return;
            }

            if (!password.equals(confirm)) {
                logger.warning("Password mismatch during registration for email: " + email);
                System.out.println("Passwords do not match");
                return;
            }

            if (!role.equals(ROLE_JOB_SEEKER) && !role.equals(ROLE_EMPLOYER)) {
                logger.warning("Invalid role during registration: " + role);
                System.out.println("Invalid role selected");
                return;
            }

            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);

            boolean success = controller.register(user);

            if (!success) {
                logger.warning("Registration failed - email may already exist: " + email);
                System.out.println("Registration failed (email may already exist)");
                return;
            }

            logger.info("User registered successfully: " + email);
            System.out.println("Registration successful!");

            System.out.print("Do you want to login now? (Y/N): ");
            if (sc.nextLine().equalsIgnoreCase("Y")) {
                loginFlow(sc, controller);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during registration", e);
            System.out.println("Error during registration");
        }
    }

    // ================= FORGOT PASSWORD FLOW =================
    private static void forgotPasswordFlow(Scanner sc, UserAccountController controller) {

        logger.info("Forgot password flow started");

        System.out.println("\n==== FORGOT PASSWORD ====");

        System.out.print("Enter registered email: ");
        String email = sc.nextLine().trim();

        if (email.isEmpty()) {
            logger.warning("Forgot password attempted with empty email");
            System.out.println("Email cannot be empty");
            return;
        }

        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine().trim();

        System.out.print("Confirm new password: ");
        String confirmPassword = sc.nextLine().trim();

        if (!newPassword.equals(confirmPassword)) {
            logger.warning("Password mismatch during reset for email: " + email);
            System.out.println("Passwords do not match");
            return;
        }

        boolean success = controller.resetPassword(email, newPassword);

        if (success) {
            logger.info("Password reset successful for email: " + email);
            System.out.println("Password reset successful");
        } else {
            logger.warning("Password reset failed - email not found: " + email);
            System.out.println("Email not found");
        }
    }
}