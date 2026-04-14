package dao;

import datasource.MariaDbConnection;
import entity.ChangeDept;
import entity.Employee;
import entity.EmployeeTranslation;
import entity.LanguageManager;
import jakarta.persistence.EntityManager;
import javafx.scene.control.TextField;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class EmployeeTranslationDao  extends BaseDao<EmployeeTranslation>{

    LanguageManager lang = LanguageManager.getInstance();

    public EmployeeTranslationDao() {
        super(EmployeeTranslation.class);
    }

    private String convertDigits(String input, Locale locale) {
        if (!locale.getLanguage().equals("ar") && !locale.getLanguage().equals("fa")) {
            return input;
        }

        char[][] digits = {
                "0123456789".toCharArray(),
                locale.getLanguage().equals("ar")
                        ? "٠١٢٣٤٥٦٧٨٩".toCharArray()
                        : "۰۱۲۳۴۵۶۷۸۹".toCharArray()
        };

        StringBuilder sb = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(digits[1][c - '0']);
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }


    private String formatDate(LocalDate date, String language) {

        String code = lang.getAbreviation(language);
        Locale locale;

        switch (code) {
            case "ar":
                locale = new Locale("ar");
                break;

            case "fa":
                locale = new Locale("fa", "AF");
                break;

            case "es":
                locale = new Locale("es", "ES");
                break;

            default:
                locale = Locale.ENGLISH;
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("d MMMM yyyy", locale);

        String formatted = date.format(formatter);

        return convertDigits(formatted, locale);
    }

    public String formatNumber(double salary, String language) {
        String code = lang.getAbreviation(language);

        Locale locale = switch (code) {
            case "ar" -> new Locale.Builder()
                    .setLanguage("ar")
                    .setRegion("AE")
                    .setUnicodeLocaleKeyword("nu", "arab") // 🔥 THIS LINE
                    .build();
            case "es" -> new Locale("es", "ES");
            case "fa" -> new Locale("fa", "AF");
            default -> Locale.US;
        };

        NumberFormat formatter = NumberFormat.getInstance(locale);
        return formatter.format(salary);
    }




    public void submitCreateEmployeeTranslation(String genderCombo, String contractCombo, TextField jobTitleField, Employee newEmp){
        String originalLanguage = lang.getLanguage();

        String genderKey = lang.getKey(genderCombo);
        String contractKey = lang.getKey(contractCombo);

        List<String> languages = List.of("arabic", "spanish", "english", "persia");


        try {
            for (String language : languages) {
                lang.setLanguage(language);

                String translatedGender = lang.getTranslatedText(genderKey);
                String translatedContract = lang.getTranslatedText(contractKey);

                String startDateText = formatDate(newEmp.getStartDate(), language);
                String formattedSalary = formatNumber(newEmp.getSalary(), language);


                EmployeeTranslation translation = new EmployeeTranslation(
                        lang.getAbreviation(language),
                        jobTitleField.getText(),
                        translatedContract,
                        translatedGender,
                        newEmp,
                        startDateText,
                        formattedSalary
                );

                create(translation);
            }
        } finally {
            lang.setLanguage(originalLanguage);
        }
    }



    public void deleteByEmployee(Employee employee) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM EmployeeTranslation et WHERE et.employee = :emp")
                    .setParameter("emp", employee)
                    .executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }


}
