package com.chakray.userapi.service;

import com.chakray.userapi.model.Address;
import com.chakray.userapi.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import com.chakray.userapi.exception.ApiException;
import org.springframework.http.HttpStatus;
import java.util.regex.Pattern;


@Service
public class UserService {

    private final List<User> users = new ArrayList<>();
    private final CryptoService cryptoService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
        Pattern.CASE_INSENSITIVE
        );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
                "^(?:\\+\\d{1,3}[ -]?)?(?:\\d[ -]?){9}\\d$"
        );

      private static final Pattern TAX_ID_PATTERN = Pattern.compile(
                "^[A-ZÑ&]{4}\\d{6}[A-Z0-9]{3}$"
        );

    public UserService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
        List<Address> addresses = List.of(
                new Address(1L, "workaddress", "Victor hugo 96", "MEX"),
                new Address(2L, "homeaddress", "insurgentes", "MEX")
        );

        users.add(new User(
                UUID.randomUUID(),
                "alberto@mail.com",
                "alberto1",
                "+1 55 555 555 55",
                cryptoService.encrypt("7c4a8d09ca3762af61e59520943dc26494f8941b"),
                "AARR990101XXX",
                currentMadagascarTimestamp(),
                addresses
        ));

        users.add(new User(
                UUID.randomUUID(),
                "user2@mail.com",
                "user2",
                "+52 55 1234 5678",
               cryptoService.encrypt("password2"),
                "BERR980202XXX",
                currentMadagascarTimestamp(),
                addresses
        ));

        users.add(new User(
                UUID.randomUUID(),
                "user3@mail.com",
                "user3",
                "+52 55 8765 4321",
                cryptoService.encrypt("password3"),
                "CARR970303XXX",
                currentMadagascarTimestamp(),
                addresses
        ));
    }


    public void deleteUser(UUID id) {
        boolean removed = users.removeIf(user -> user.getId().equals(id));

        if (!removed) {
            throw new ApiException(HttpStatus.NOT_FOUND, "User not found");
        }
}

    public User addUser(User user) {
        boolean taxIdExists = users.stream()
                .anyMatch(existingUser ->
                        existingUser.getTaxId().equalsIgnoreCase(user.getTaxId())
                );

        if (taxIdExists) {
                throw new ApiException(
                        HttpStatus.CONFLICT,
                        "tax_id must be unique"
                );
        }

        String createdAt = currentMadagascarTimestamp();

        user.setId(UUID.randomUUID());
        user.setCreatedAt(createdAt);
        user.setPassword(cryptoService.encrypt(user.getPassword()));

        users.add(user);
        return user;
    }

    public User authenticate(String taxId, String password) {
        return users.stream()
                .filter(user -> user.getTaxId().equalsIgnoreCase(taxId))
                .filter(user ->
                        cryptoService.decrypt(user.getPassword()).equals(password)
                )
                .findFirst()
                .orElseThrow(() ->
                        new ApiException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid credentials"
                        )
                );
}

    public User updateUser(UUID id, User changes) {
        User user = users.stream()
                .filter(existingUser -> existingUser.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                    new ApiException(HttpStatus.NOT_FOUND, "User not found")
                );

        if (changes.getEmail() != null) {
                if (!EMAIL_PATTERN.matcher(changes.getEmail()).matches()) {
                        throw new ApiException(
                                HttpStatus.BAD_REQUEST,
                                "email format is invalid"
                        );
                }

                user.setEmail(changes.getEmail());
        }

        if (changes.getName() != null) {
                user.setName(changes.getName());
        }

        if (changes.getPhone() != null) {
                if (!PHONE_PATTERN.matcher(changes.getPhone()).matches()) {
                        throw new ApiException(
                                HttpStatus.BAD_REQUEST,
                                "phone must contain 10 digits and may include a country code"
                        );
                }



                user.setPhone(changes.getPhone());
        }

        if (changes.getTaxId() != null) {
                if (!TAX_ID_PATTERN.matcher(changes.getTaxId()).matches()) {
                        throw new ApiException(
                                HttpStatus.BAD_REQUEST,
                                "tax_id must have a valid RFC format"
                        );
                }


                boolean taxIdExists = users.stream()
                        .anyMatch(existingUser ->
                                !existingUser.getId().equals(id)
                                        && existingUser.getTaxId()
                                        .equalsIgnoreCase(changes.getTaxId())
                        );

                if (taxIdExists) {
                        throw new ApiException(
                                HttpStatus.CONFLICT,
                                "tax_id must be unique"
                        );
                }

                user.setTaxId(changes.getTaxId());
        }

        if (changes.getPassword() != null) {
               user.setPassword(cryptoService.encrypt(changes.getPassword()));
        }

        if (changes.getAddresses() != null) {
          user.setAddresses(changes.getAddresses());
        }

        return user;
 }

    public List<User> getUsers(String sortedBy) {
        List<User> result = new ArrayList<>(users);

        if (sortedBy == null || sortedBy.isBlank()) {
                return result;
        }

        Comparator<User> comparator = switch (sortedBy) {
                case "email" -> Comparator.comparing(User::getEmail);
                case "id" -> Comparator.comparing(User::getId);
                case "name" -> Comparator.comparing(User::getName);
                case "phone" -> Comparator.comparing(User::getPhone);
                case "tax_id" -> Comparator.comparing(User::getTaxId);
                case "created_at" -> Comparator.comparing(User::getCreatedAt);
                default -> throw new IllegalArgumentException(
                        "Invalid sortedBy value: " + sortedBy
                );
        };

        result.sort(comparator);
        return result;
   }


   public List<User> filterUsers(String filter) {
    if (filter == null || filter.isBlank()) {
        throw new IllegalArgumentException("Filter cannot be empty");
    }

    String normalizedFilter = filter.replace("+", " ").trim();
    String[] parts = normalizedFilter.split("\\s+", 3);

    if (parts.length != 3) {
        throw new IllegalArgumentException(
                "Filter format must be: field operator value"
        );
    }

    String field = parts[0];
    String operator = parts[1];
    String expectedValue = parts[2];

    return users.stream()
            .filter(user -> {
                String actualValue = getFieldValue(user, field);

                return switch (operator) {
                    case "co" -> actualValue.contains(expectedValue);
                    case "eq" -> actualValue.equals(expectedValue);
                    case "sw" -> actualValue.startsWith(expectedValue);
                    case "ew" -> actualValue.endsWith(expectedValue);
                    default -> throw new IllegalArgumentException(
                            "Invalid filter operator: " + operator
                    );
                };
            })
            .toList();
  }

    private String currentMadagascarTimestamp() {
        return LocalDateTime
                .now(ZoneId.of("Indian/Antananarivo"))
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

   private String getFieldValue(User user, String field) {
        return switch (field) {
                case "email" -> user.getEmail();
                case "id" -> user.getId().toString();
                case "name" -> user.getName();
                case "phone" -> user.getPhone();
                case "tax_id" -> user.getTaxId();
                case "created_at" -> user.getCreatedAt();
                default -> throw new IllegalArgumentException(
                        "Invalid filter field: " + field
                );
        };
   }
}
