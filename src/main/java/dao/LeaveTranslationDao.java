package dao;

import datasource.MariaDbConnection;
import entity.LanguageManager;
import entity.Leave;
import entity.LeaveTranslation;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class LeaveTranslationDao extends BaseDao<LeaveTranslation> {

    private final LanguageManager lang = LanguageManager.getInstance();

    public LeaveTranslationDao() {
        super(LeaveTranslation.class);
    }

    private String formatDate(LocalDate date, String language) {
        if (date == null) return "N/A";

        Locale locale = switch (language) {
            case "arabic" -> new Locale("ar");
            case "spanish" -> new Locale("es");
            case "persia"  -> new Locale("fa");
            default        -> Locale.ENGLISH;
        };

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMMM yyyy", locale);

        return date.format(formatter);
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

                String durationText = leave.getDuration() + "";

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