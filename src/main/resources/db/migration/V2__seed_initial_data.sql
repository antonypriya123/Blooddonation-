-- =======================================================
-- Flyway Database Migration: V2__seed_initial_data.sql
-- BloodConnect Blood Donation Management System
-- Initial Seed Data: Default Admin, Sample Donors & Requests
-- All passwords are encrypted with BCrypt (password: Password@123)
-- BCrypt: $2a$10$7EqJtq98hPqEX7fNZaFWoOa1iPsm/mRknq1.Wd55K4Jj3b/l17Lca
-- =======================================================

-- 1. Insert Default Administrator
INSERT INTO users (id, full_name, email, password, mobile_number, date_of_birth, gender, city, state, role, active)
VALUES (1, 'System Administrator', 'admin@bloodconnect.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOa1iPsm/mRknq1.Wd55K4Jj3b/l17Lca', '+1-555-0100', '1988-06-15', 'MALE', 'New York', 'New York', 'ADMIN', TRUE)
ON DUPLICATE KEY UPDATE id=id;

-- 2. Insert Sample Donors
INSERT INTO users (id, full_name, email, password, mobile_number, date_of_birth, gender, city, state, role, active)
VALUES 
(2, 'Dr. Robert Carter', 'donor.robert@bloodconnect.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOa1iPsm/mRknq1.Wd55K4Jj3b/l17Lca', '+1-555-0101', '1992-03-12', 'MALE', 'New York', 'New York', 'DONOR', TRUE),
(3, 'Sarah Jenkins', 'donor.sarah@bloodconnect.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOa1iPsm/mRknq1.Wd55K4Jj3b/l17Lca', '+1-555-0102', '1995-08-24', 'FEMALE', 'Boston', 'Massachusetts', 'DONOR', TRUE),
(4, 'Michael Chen', 'donor.michael@bloodconnect.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOa1iPsm/mRknq1.Wd55K4Jj3b/l17Lca', '+1-555-0103', '1990-11-05', 'MALE', 'Chicago', 'Illinois', 'DONOR', TRUE),
(5, 'Elena Rodriguez', 'donor.elena@bloodconnect.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOa1iPsm/mRknq1.Wd55K4Jj3b/l17Lca', '+1-555-0104', '1996-01-19', 'FEMALE', 'Los Angeles', 'California', 'DONOR', TRUE),
(6, 'David Williams', 'donor.david@bloodconnect.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOa1iPsm/mRknq1.Wd55K4Jj3b/l17Lca', '+1-555-0105', '1987-09-30', 'MALE', 'Houston', 'Texas', 'DONOR', TRUE)
ON DUPLICATE KEY UPDATE id=id;

-- 3. Insert Donor Profiles
INSERT INTO donor_profiles (id, user_id, blood_group, last_donation_date, is_available, total_donations, pin_code, address)
VALUES
(1, 2, 'O+', '2024-01-15', TRUE, 4, '10001', '452 Lexington Ave, Apt 4B'),
(2, 3, 'A+', '2023-11-20', TRUE, 2, '02108', '88 Beacon St'),
(3, 4, 'B-', '2024-02-10', TRUE, 5, '60601', '120 N Michigan Ave'),
(4, 5, 'AB+', '2023-10-05', TRUE, 1, '90012', '742 Evergreen Terrace'),
(5, 6, 'O-', '2024-03-01', TRUE, 6, '77002', '310 Main Street')
ON DUPLICATE KEY UPDATE id=id;

-- 4. Insert Sample Requesters
INSERT INTO users (id, full_name, email, password, mobile_number, date_of_birth, gender, city, state, role, active)
VALUES
(7, 'Emily Watson', 'requester.emily@bloodconnect.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOa1iPsm/mRknq1.Wd55K4Jj3b/l17Lca', '+1-555-0106', '1994-04-18', 'FEMALE', 'New York', 'New York', 'REQUESTER', TRUE),
(8, 'James Peterson', 'requester.james@bloodconnect.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOa1iPsm/mRknq1.Wd55K4Jj3b/l17Lca', '+1-555-0107', '1985-12-03', 'MALE', 'Boston', 'Massachusetts', 'REQUESTER', TRUE)
ON DUPLICATE KEY UPDATE id=id;

-- 5. Insert Sample Blood Requests
INSERT INTO blood_requests (id, requester_id, patient_name, blood_group, units_required, hospital_name, hospital_address, city, state, contact_number, required_date, urgency_level, status, additional_info)
VALUES
(1, 7, 'Alexander Watson', 'O+', 2, 'Mount Sinai Hospital', '1468 Madison Ave', 'New York', 'New York', '+1-555-0106', '2024-11-10', 'CRITICAL', 'PENDING', 'Emergency open-heart surgery required immediately.'),
(2, 7, 'Clara Watson', 'A+', 1, 'NYU Langone Health', '550 1st Ave', 'New York', 'New York', '+1-555-0106', '2024-11-15', 'HIGH', 'ACCEPTED', 'Thalassemia major patient regular transfusion.'),
(3, 8, 'Lucas Peterson', 'O-', 3, 'Massachusetts General Hospital', '55 Fruit St', 'Boston', 'Massachusetts', '+1-555-0107', '2024-11-08', 'CRITICAL', 'PENDING', 'Urgent trauma unit requirement.'),
(4, 8, 'Grace Peterson', 'B-', 2, 'Boston Children\'s Hospital', '300 Longwood Ave', 'Boston', 'Massachusetts', '+1-555-0107', '2024-11-20', 'MEDIUM', 'FULFILLED', 'Scheduled pediatric orthopedic procedure.')
ON DUPLICATE KEY UPDATE id=id;

-- 6. Insert Sample Donation History
INSERT INTO donation_history (id, donor_id, blood_request_id, donation_date, units_donated, hospital_name, remarks)
VALUES
(1, 2, NULL, '2024-01-15', 1, 'Mount Sinai Hospital', 'Routine voluntary blood donation drive.'),
(2, 6, 4, '2024-03-01', 2, 'Boston Children\'s Hospital', 'Donated on urgent appeal for pediatric surgery.')
ON DUPLICATE KEY UPDATE id=id;

-- 7. Insert Initial Notifications
INSERT INTO notifications (id, user_id, title, message, is_read, type)
VALUES
(1, 1, 'System Welcome', 'Welcome to BloodConnect Administrator Console. Monitor live metrics and user activity.', FALSE, 'SYSTEM'),
(2, 2, 'Welcome Donor', 'Thank you for registering as a blood donor. Keep your availability updated.', FALSE, 'DONOR'),
(3, 7, 'Request Submitted', 'Your blood request for Alexander Watson (O+) has been broadcasted to available donors.', FALSE, 'REQUEST')
ON DUPLICATE KEY UPDATE id=id;
