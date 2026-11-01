package com.bloodconnect.config;

import com.bloodconnect.entity.BloodRequest;
import com.bloodconnect.entity.DonorProfile;
import com.bloodconnect.entity.User;
import com.bloodconnect.enums.Gender;
import com.bloodconnect.enums.RequestStatus;
import com.bloodconnect.enums.Role;
import com.bloodconnect.enums.UrgencyLevel;
import com.bloodconnect.repository.BloodRequestRepository;
import com.bloodconnect.repository.DonorProfileRepository;
import com.bloodconnect.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DonorProfileRepository donorProfileRepository,
                           BloodRequestRepository bloodRequestRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.donorProfileRepository = donorProfileRepository;
        this.bloodRequestRepository = bloodRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        System.out.println("[BloodConnect] Seeding initial demo accounts...");

        // 1. Admin
        User admin = new User();
        admin.setFullName("System Administrator");
        admin.setEmail("admin@bloodconnect.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setMobileNumber("9876543210");
        admin.setDateOfBirth(LocalDate.of(1988, 1, 15));
        admin.setGender(Gender.MALE);
        admin.setCity("Chennai");
        admin.setState("Tamil Nadu");
        admin.setRole(Role.ADMIN);
        admin.setActive(true);
        userRepository.save(admin);

        // 2. Donor 1 (John Doe - O+)
        User donor1 = new User();
        donor1.setFullName("John Doe");
        donor1.setEmail("john.donor@example.com");
        donor1.setPassword(passwordEncoder.encode("Donor@123"));
        donor1.setMobileNumber("9876543211");
        donor1.setDateOfBirth(LocalDate.of(1995, 5, 20));
        donor1.setGender(Gender.MALE);
        donor1.setCity("Nagercoil");
        donor1.setState("Tamil Nadu");
        donor1.setRole(Role.DONOR);
        donor1.setActive(true);
        User savedDonor1 = userRepository.save(donor1);

        DonorProfile profile1 = new DonorProfile();
        profile1.setUser(savedDonor1);
        profile1.setBloodGroup("O+");
        profile1.setAvailable(true);
        profile1.setTotalDonations(5);
        profile1.setLastDonationDate(LocalDate.now().minusMonths(4));
        profile1.setPinCode("629001");
        profile1.setAddress("Court Road, Nagercoil");
        donorProfileRepository.save(profile1);

        // 3. Donor 2 (Sarah Connor - A+)
        User donor2 = new User();
        donor2.setFullName("Sarah Connor");
        donor2.setEmail("sarah.donor@example.com");
        donor2.setPassword(passwordEncoder.encode("Donor@123"));
        donor2.setMobileNumber("9876543212");
        donor2.setDateOfBirth(LocalDate.of(1998, 8, 14));
        donor2.setGender(Gender.FEMALE);
        donor2.setCity("Chennai");
        donor2.setState("Tamil Nadu");
        donor2.setRole(Role.DONOR);
        donor2.setActive(true);
        User savedDonor2 = userRepository.save(donor2);

        DonorProfile profile2 = new DonorProfile();
        profile2.setUser(savedDonor2);
        profile2.setBloodGroup("A+");
        profile2.setAvailable(true);
        profile2.setTotalDonations(3);
        profile2.setLastDonationDate(LocalDate.now().minusMonths(2));
        profile2.setPinCode("600001");
        profile2.setAddress("Anna Salai, Chennai");
        donorProfileRepository.save(profile2);

        // 4. Donor 3 (B+ Donor)
        User donor3 = new User();
        donor3.setFullName("Ramesh Kumar");
        donor3.setEmail("ramesh.donor@example.com");
        donor3.setPassword(passwordEncoder.encode("Donor@123"));
        donor3.setMobileNumber("9876543213");
        donor3.setDateOfBirth(LocalDate.of(1992, 11, 28));
        donor3.setGender(Gender.MALE);
        donor3.setCity("Nagercoil");
        donor3.setState("Tamil Nadu");
        donor3.setRole(Role.DONOR);
        donor3.setActive(true);
        User savedDonor3 = userRepository.save(donor3);

        DonorProfile profile3 = new DonorProfile();
        profile3.setUser(savedDonor3);
        profile3.setBloodGroup("B+");
        profile3.setAvailable(true);
        profile3.setTotalDonations(2);
        profile3.setLastDonationDate(LocalDate.now().minusMonths(6));
        profile3.setPinCode("629002");
        profile3.setAddress("Cape Road, Nagercoil");
        donorProfileRepository.save(profile3);

        // 5. Requester
        User requester = new User();
        requester.setFullName("Michael Brown");
        requester.setEmail("michael.requester@example.com");
        requester.setPassword(passwordEncoder.encode("Request@123"));
        requester.setMobileNumber("9876543214");
        requester.setDateOfBirth(LocalDate.of(1985, 3, 10));
        requester.setGender(Gender.MALE);
        requester.setCity("Nagercoil");
        requester.setState("Tamil Nadu");
        requester.setRole(Role.REQUESTER);
        requester.setActive(true);
        User savedRequester = userRepository.save(requester);

        // 6. Blood Requests
        BloodRequest req1 = new BloodRequest();
        req1.setRequester(savedRequester);
        req1.setPatientName("Priya Sharma");
        req1.setBloodGroup("B+");
        req1.setUnitsRequired(2);
        req1.setHospitalName("Kanyakumari Govt Medical College Hospital");
        req1.setHospitalAddress("Asaripallam, Nagercoil");
        req1.setCity("Nagercoil");
        req1.setState("Tamil Nadu");
        req1.setContactNumber("9876543214");
        req1.setRequiredDate(LocalDate.now().plusDays(1));
        req1.setUrgencyLevel(UrgencyLevel.CRITICAL);
        req1.setStatus(RequestStatus.PENDING);
        req1.setAdditionalInfo("Urgent surgery scheduled. Need 2 units of B+ blood.");
        bloodRequestRepository.save(req1);

        BloodRequest req2 = new BloodRequest();
        req2.setRequester(savedRequester);
        req2.setPatientName("Anand Verma");
        req2.setBloodGroup("O+");
        req2.setUnitsRequired(1);
        req2.setHospitalName("Apollo Hospitals");
        req2.setHospitalAddress("Greams Road, Chennai");
        req2.setCity("Chennai");
        req2.setState("Tamil Nadu");
        req2.setContactNumber("9876543215");
        req2.setRequiredDate(LocalDate.now().plusDays(2));
        req2.setUrgencyLevel(UrgencyLevel.HIGH);
        req2.setStatus(RequestStatus.PENDING);
        req2.setAdditionalInfo("Emergency requirement for dengue patient.");
        bloodRequestRepository.save(req2);

        System.out.println("[BloodConnect] Demo accounts and requests seeded successfully!");
    }
}
