package dao;

import datasource.MariaDbConnection;
import entity.LanguageManager;
import entity.Leave;
import entity.LeaveTranslation;
import jakarta.persistence.EntityManager;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class LeaveTranslationDao extends BaseDao<LeaveTranslation> {

    private final LanguageManager lang = LanguageManager.getInstance();

    public LeaveTranslationDao() {
        super(LeaveTranslation.class);
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

    // CREATE translations for all languages (reason only)
    public void submitCreateLeaveTranslation(String type, Leave leave) {

        String originalLanguage = lang.getLanguage();

        String typeKey = lang.getKey(type);

        List<String> languages = List.of("arabic", "spanish", "english", "persia");

        try {
            for (String language : languages) {
                lang.setLanguage(language);

                String typeText = lang.getTranslatedText(typeKey);

                // Format date in each locale
                String startDateText = formatDate(leave.getStartDate(), language);
                String endDateText = formatDate(leave.getEndDate(), language);

                String durationText = formatNumber(leave.getDuration(), language);

                LeaveTranslation translation = new LeaveTranslation(
                        lang.getAbreviation(language),
                        typeText,
                        leave,
                        startDateText,
                        endDateText,
                        durationText
                );

                create(translation);
            }
        } finally {
            lang.setLanguage(originalLanguage);
        }
    }




    // DELETE translations by leave
    public void deleteByLeave(Leave leave) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();

            em.createQuery(
                            "DELETE FROM LeaveTranslation lt WHERE lt.leave = :leave"
                    )
                    .setParameter("leave", leave)
                    .executeUpdate();

            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }



}