package dao;

import datasource.MariaDbConnection;
import entity.Department;
import entity.DepartmentTranslation;
import entity.LanguageManager;
import jakarta.persistence.EntityManager;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DepartmentTranslationDao extends BaseDao<DepartmentTranslation> {

    private final LanguageManager lang = LanguageManager.getInstance();

    public DepartmentTranslationDao() {
        super(DepartmentTranslation.class);
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


    // CREATE translations for all languages (same pattern as Employee)
    public void submitCreateDepartmentTranslation(
            String departmentName,
            Department department
    ) {
        String originalLanguage = lang.getLanguage();

        String departmentNameKey = lang.getKey(departmentName);


        List<String> languages = List.of("arabic", "spanish", "english", "persia");

        try {
            for (String language : languages) {
                lang.setLanguage(language);

                String departmentNameTrans = lang.getTranslatedText(departmentNameKey);
                String capacity = formatNumber(department.getCapacity(), language);
                String startDateText = formatDate(department.getCreationDate(), language);

                DepartmentTranslation translation = new DepartmentTranslation(
                        lang.getAbreviation(language),
                        departmentNameTrans,
                        department,
                        startDateText,
                        capacity
                );

                create(translation);
            }
        } finally {
            lang.setLanguage(originalLanguage);
        }
    }

    // DELETE all translations for a department
    public void deleteByDepartment(Department department) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();

            em.createQuery(
                            "DELETE FROM DepartmentTranslation dt WHERE dt.department = :dept"
                    )
                    .setParameter("dept", department)
                    .executeUpdate();

            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }


}