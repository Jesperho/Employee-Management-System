package dao;

import datasource.MariaDbConnection;
import entity.ChangeDept;
import entity.Employee;
import entity.EmployeeTranslation;
import entity.LanguageManager;
import jakarta.persistence.EntityManager;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class EmployeeTranslationDao  extends BaseDao<EmployeeTranslation>{

    LanguageManager lang = LanguageManager.getInstance();

    public EmployeeTranslationDao() {
        super(EmployeeTranslation.class);
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

                EmployeeTranslation translation = new EmployeeTranslation(
                        lang.getAbreviation(language),
                        jobTitleField.getText(),
                        translatedContract,
                        translatedGender,
                        newEmp,
                        startDateText,
                        newEmp.getSalary()
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
