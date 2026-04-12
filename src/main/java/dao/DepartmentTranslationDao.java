package dao;

import datasource.MariaDbConnection;
import entity.Department;
import entity.DepartmentTranslation;
import entity.LanguageManager;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DepartmentTranslationDao extends BaseDao<DepartmentTranslation> {

    private final LanguageManager lang = LanguageManager.getInstance();

    public DepartmentTranslationDao() {
        super(DepartmentTranslation.class);
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

                String startDateText = formatDate(department.getCreationDate(), language);

                DepartmentTranslation translation = new DepartmentTranslation(
                        lang.getAbreviation(language),
                        departmentNameTrans,
                        department,
                        startDateText,
                        department.getCapacity()
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