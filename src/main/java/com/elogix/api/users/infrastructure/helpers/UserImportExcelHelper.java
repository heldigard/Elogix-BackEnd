package com.elogix.api.users.infrastructure.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.elogix.api.shared.infraestructure.helpers.ExcelHelper;
import com.elogix.api.users.infrastructure.driven_adapters.jpa_repository.office.OfficeData;
import com.elogix.api.users.infrastructure.driven_adapters.jpa_repository.office.OfficeDataJpaRepository;
import com.elogix.api.users.infrastructure.driven_adapters.jpa_repository.role.RoleData;
import com.elogix.api.users.infrastructure.driven_adapters.jpa_repository.role.RoleDataJpaRepository;
import com.elogix.api.users.infrastructure.driven_adapters.jpa_repository.user.UserData;
import com.elogix.api.users.infrastructure.driven_adapters.jpa_repository.user.UserDataJpaRepository;
import com.elogix.api.users.infrastructure.entry_points.dto.UserExcelResponse;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserImportExcelHelper {
    private final ExcelHelper excelHelper;
    private final UserDataJpaRepository userRepository;
    private final RoleDataJpaRepository roleRepository;
    private final OfficeDataJpaRepository officeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserExcelResponse excelToUsers(InputStream inputStream) {
        UserExcelResponse response = new UserExcelResponse();

        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            Set<String> userList = new HashSet<>();
            Map<Integer, String> mapColumns = excelHelper.mapColumns(rows.next());

            List<UserData> existingList = userRepository.findAll();
            Map<String, UserData> mappedUsernames = excelHelper.mapEntities(existingList, UserData::getUsername);

            List<RoleData> existingRoles = roleRepository.findAll();
            Map<String, RoleData> mappedUserRoles = excelHelper.mapEntities(existingRoles,
                    data -> data.getName().toString());

            List<OfficeData> existingOffices = officeRepository.findAll();
            Map<String, OfficeData> mappedUserOffices = excelHelper.mapEntities(existingOffices,
                    data -> data.getName().toString());

            int numOfNonEmptyCells = excelHelper.getNumberOfNonEmptyCells(sheet, 0);

            for (int rowIndex = 0; rowIndex < numOfNonEmptyCells - 1; rowIndex++) {
                Row currentRow = rows.next();

                String cellValue = excelHelper.getCleanCellValue(currentRow.getCell(0), false);
                if (userList.contains(cellValue)) {
                    continue;
                }

                UserData user;
                if (mappedUsernames.containsKey(cellValue.toLowerCase())) {
                    user = mappedUsernames.get(cellValue);
                    user.setUpdatedAt(Instant.now());
                } else {
                    user = new UserData();
                    user.setCreatedAt(Instant.now());
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    int cellIdx = currentCell.getColumnIndex();
                    user = _setupUserFromCell(
                            cellIdx,
                            currentCell,
                            user,
                            response.getErrors(),
                            mapColumns,
                            mappedUserRoles,
                            mappedUserOffices);
                }

                if (!userList.contains(user.getUsername())
                        && user.getPassword() != null
                        && !user.getPassword().isEmpty()) {
                    response.getUsers().add(user);
                    userList.add(user.getUsername());
                }
            }

            workbook.close();

            return response;

        } catch (IOException e) {
            response.getErrors().add(e.getMessage());
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

    private UserData _setupUserFromCell(
            int cellIdx,
            Cell cell,
            UserData user,
            Set<String> errors,
            Map<Integer, String> mapColumns,
            Map<String, RoleData> mappedUserRoles,
            Map<String, OfficeData> mappedUserOffices) {
        final String cellValue = excelHelper.getCleanCellValue(cell, false);

        String columnName = mapColumns.get(cellIdx);

        if (cellValue.isEmpty() || cellValue.contains("N/A") || cellValue.equals("0")) {
            return user;
        }

        switch (columnName) {
            case "Username":
                if (user.getUsername() == null) {
                    user.setUsername(cellValue.toLowerCase());
                }

                return user;

            case "Password":
                if (cellValue.length() >= 6) {
                    user.setPassword(passwordEncoder.encode(cellValue));
                } else {
                    errors.add("Error: Password debe tener al menos 6 caracteres: " + cellValue);
                }

                return user;

            case "Nombre":
                user.setFirstName(cellValue.toUpperCase());

                return user;

            case "Apellido":
                user.setLastName(cellValue.toUpperCase());

                return user;

            case "Email":
                String email = excelHelper.getCleanEmailAddress(cellValue);
                if (EmailValidator.isValidEmail(email)) {
                    user.setEmail(email);
                } else {
                    errors.add("Error: Email invalido: " + cellValue);
                }

                return user;

            case "Roles":
                String[] roles = cellValue.split(",");
                for (String role : roles) {
                    role = role.trim().toUpperCase();
                    if (mappedUserRoles.containsKey(role)) {
                        user.getRoles().add(mappedUserRoles.get(role));
                    }
                }

                return user;

            case "Oficina":
                String value = cellValue.toUpperCase();

                OfficeData office;
                if (mappedUserOffices.containsKey(value)) {
                    office = mappedUserOffices.get(value);
                } else {
                    OfficeData.OfficeDataBuilder ub = OfficeData.builder();
                    office = ub.name(value).build();
                    mappedUserOffices.put(value, office);
                }

                user.setOffice(office);

                return user;

            case "Telefono":
                user.setPhone(cellValue);

                return user;

            default:
                return user;
        }
    }
}
