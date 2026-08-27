package com.moneymanager.service;

import com.moneymanager.dto.ExpenseDto;
import com.moneymanager.entity.CategoryEntity;
import com.moneymanager.entity.ProfileEntity;
import com.moneymanager.repositories.CategoryRepository;
import com.moneymanager.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;
    private final CategoryRepository categoryRepository;


    @Value("${money.manager.frontend.url}")
    private String frontEndUrl;

//    @Scheduled(cron = "0 * * * * *", zone = "IST")
    @Scheduled(cron = "0 0 22 * * *", zone = "IST") // every day at 10 pm
    public void sendIncomeExpenseRemainder(){
        log.info("Job Started: SendDailyIncomeExpenseRemainder()");
        List<ProfileEntity> result = profileRepository.findAll();
        for (ProfileEntity profile : result){
            String emailBody =
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<meta charset='UTF-8'>" +
                            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                            "</head>" +

                            "<body style='" +
                            "margin:0;" +
                            "padding:0;" +
                            "background-color:#0b0d10;" +
                            "font-family:Arial,Helvetica,sans-serif;" +
                            "'>" +

                            "<div style='" +
                            "padding:40px 20px;" +
                            "background-color:#0b0d10;" +
                            "'>" +

                            "<div style='" +
                            "max-width:600px;" +
                            "margin:0 auto;" +
                            "background-color:#111418;" +
                            "border:1px solid #b8862c;" +
                            "border-radius:16px;" +
                            "padding:40px 35px;" +
                            "'>" +

                            // Greeting
                            "<h1 style='" +
                            "margin:0 0 20px;" +
                            "font-family:Georgia,serif;" +
                            "font-size:28px;" +
                            "font-weight:normal;" +
                            "text-align:center;" +
                            "color:#ffffff;" +
                            "'>" +

                            "Hi <span style='color:#d4a64a;'>" +
                            profile.getFullName() +
                            "</span>," +

                            "</h1>" +

                            // Divider
                            "<div style='" +
                            "width:80px;" +
                            "height:1px;" +
                            "background-color:#d4a64a;" +
                            "margin:0 auto 30px;" +
                            "'></div>" +

                            // Heading
                            "<h2 style='" +
                            "margin:0 0 20px;" +
                            "font-family:Georgia,serif;" +
                            "font-size:21px;" +
                            "font-weight:normal;" +
                            "text-align:center;" +
                            "color:#d4a64a;" +
                            "'>" +

                            "Daily Financial Reminder" +

                            "</h2>" +

                            // Message
                            "<p style='" +
                            "margin:0 0 18px;" +
                            "font-size:15px;" +
                            "line-height:1.8;" +
                            "color:#c5c7ca;" +
                            "text-align:center;" +
                            "'>" +

                            "This is a friendly reminder to add your " +
                            "income and expenses for today in " +
                            "<strong style='color:#ffffff;'>Money Manager</strong>." +

                            "</p>" +

                            "<p style='" +
                            "margin:0 0 25px;" +
                            "font-size:15px;" +
                            "line-height:1.8;" +
                            "color:#c5c7ca;" +
                            "text-align:center;" +
                            "'>" +

                            "Keeping your financial records up to date " +
                            "helps you stay organized and in control " +
                            "of your finances." +

                            "</p>" +

                            // Button
                            "<div style='text-align:center;margin:30px 0;'>" +

                            "<a href='" + frontEndUrl + "'" +
                            " style='" +
                            "display:inline-block;" +
                            "padding:14px 28px;" +
                            "background-color:#d4a64a;" +
                            "color:#111418;" +
                            "text-decoration:none;" +
                            "font-size:14px;" +
                            "font-weight:bold;" +
                            "border-radius:8px;" +
                            "'>" +

                            "Add Your Transactions" +

                            "</a>" +

                            "</div>" +

                            // URL fallback
                            "<p style='" +
                            "margin:20px 0 0;" +
                            "font-size:12px;" +
                            "line-height:1.6;" +
                            "color:#777d84;" +
                            "text-align:center;" +
                            "word-break:break-all;" +
                            "'>" +

                            "If the button doesn't work, copy and paste this link:<br>" +
                            "<span style='color:#d4a64a;'>" +
                            frontEndUrl +
                            "</span>" +

                            "</p>" +

                            // Divider
                            "<div style='" +
                            "width:80px;" +
                            "height:1px;" +
                            "background-color:#4d3b1d;" +
                            "margin:30px auto 25px;" +
                            "'></div>" +

                            // Footer
                            "<p style='" +
                            "margin:0;" +
                            "font-family:Georgia,serif;" +
                            "font-size:14px;" +
                            "line-height:1.7;" +
                            "text-align:center;" +
                            "color:#a7a9ac;" +
                            "'>" +

                            "Best regards,<br>" +

                            "<strong style='color:#d4a64a;'>" +
                            "Money Manager Team" +
                            "</strong>" +

                            "</p>" +

                            "</div>" +
                            "</div>" +

                            "</body>" +
                            "</html>";
            emailService.sendEmail(profile.getEmail(), "Daily Remainder : Add your income and expenses", emailBody);
            log.info("Job Completed : SendDailyIncomeExpenseRemainder()");
        }
    }


//    @Scheduled(cron = "0 * * * * *", zone = "IST")
    @Scheduled(cron = "0 30 22 * * *", zone = "IST") // everyday at 10 30 pm
    public void sendDailyExpenseSummary() {

        log.info("Job Started : SendDailyExpenseSummary()");

        List<ProfileEntity> entities = profileRepository.findAll();

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        for (ProfileEntity profile : entities) {

            List<ExpenseDto> todayExpenses =
                    expenseService.getExpensesForUserOnDate(
                            profile.getId(),
                            today
                    );

            // Skip users who don't have any expenses today
            if (todayExpenses == null || todayExpenses.isEmpty()) {
                continue;
            }

            StringBuilder table = new StringBuilder();

            table.append("<table style='")
                    .append("width:100%;")
                    .append("border-collapse:collapse;")
                    .append("margin-top:20px;")
                    .append("font-family:Arial,Helvetica,sans-serif;")
                    .append("'>");

            // Table Header
            table.append("<tr style='background-color:#f2f2f2;'>");

            table.append("<th style='")
                    .append("border:1px solid #dddddd;")
                    .append("padding:10px;")
                    .append("text-align:center;")
                    .append("font-size:14px;")
                    .append("color:#333333;")
                    .append("'>")
                    .append("#")
                    .append("</th>");

            table.append("<th style='")
                    .append("border:1px solid #dddddd;")
                    .append("padding:10px;")
                    .append("text-align:left;")
                    .append("font-size:14px;")
                    .append("color:#333333;")
                    .append("'>")
                    .append("Expense")
                    .append("</th>");

            table.append("<th style='")
                    .append("border:1px solid #dddddd;")
                    .append("padding:10px;")
                    .append("text-align:right;")
                    .append("font-size:14px;")
                    .append("color:#333333;")
                    .append("'>")
                    .append("Amount")
                    .append("</th>");

            table.append("<th style='")
                    .append("border:1px solid #dddddd;")
                    .append("padding:10px;")
                    .append("text-align:left;")
                    .append("font-size:14px;")
                    .append("color:#333333;")
                    .append("'>")
                    .append("Category")
                    .append("</th>");

            table.append("</tr>");

            int i = 1;
            BigDecimal totalExpense = BigDecimal.ZERO;

            // Expense rows
            for (ExpenseDto expense : todayExpenses) {

                String categoryName = "Uncategorized";

                if (expense.getCategoryId() != null) {

                    categoryName = categoryRepository
                            .findById(expense.getCategoryId())
                            .map(CategoryEntity::getName)
                            .orElse("Uncategorized");
                }

                totalExpense = totalExpense.add(expense.getAmount());

                table.append("<tr>");

                // Number
                table.append("<td style='")
                        .append("border:1px solid #dddddd;")
                        .append("padding:10px;")
                        .append("text-align:center;")
                        .append("font-size:14px;")
                        .append("color:#555555;")
                        .append("'>")
                        .append(i++)
                        .append("</td>");

                // Expense Name
                table.append("<td style='")
                        .append("border:1px solid #dddddd;")
                        .append("padding:10px;")
                        .append("font-size:14px;")
                        .append("color:#333333;")
                        .append("'>")
                        .append(expense.getName())
                        .append("</td>");

                // Amount
                table.append("<td style='")
                        .append("border:1px solid #dddddd;")
                        .append("padding:10px;")
                        .append("text-align:right;")
                        .append("font-size:14px;")
                        .append("font-weight:bold;")
                        .append("color:#b03a2e;")
                        .append("'>")
                        .append("₹")
                        .append(expense.getAmount())
                        .append("</td>");

                // Category
                table.append("<td style='")
                        .append("border:1px solid #dddddd;")
                        .append("padding:10px;")
                        .append("font-size:14px;")
                        .append("color:#555555;")
                        .append("'>")
                        .append(categoryName)
                        .append("</td>");

                table.append("</tr>");
            }

            table.append("</table>");

            // Complete HTML email
            StringBuilder body = new StringBuilder();

            body.append("<!DOCTYPE html>")
                    .append("<html>")
                    .append("<head>")
                    .append("<meta charset='UTF-8'>")
                    .append("</head>")

                    .append("<body style='")
                    .append("margin:0;")
                    .append("padding:0;")
                    .append("background-color:#0b0d10;")
                    .append("'>")

                    .append("<div style='")
                    .append("font-family:Arial,Helvetica,sans-serif;")
                    .append("background-color:#0b0d10;")
                    .append("padding:40px 20px;")
                    .append("color:#ffffff;")
                    .append("'>")

                    .append("<div style='")
                    .append("max-width:650px;")
                    .append("margin:0 auto;")
                    .append("background-color:#111418;")
                    .append("border:1px solid #b8862c;")
                    .append("border-radius:18px;")
                    .append("padding:40px;")
                    .append("'>")

                    // Greeting
                    .append("<h1 style='")
                    .append("margin:0 0 10px;")
                    .append("font-family:Georgia,serif;")
                    .append("font-size:30px;")
                    .append("font-weight:normal;")
                    .append("color:#ffffff;")
                    .append("text-align:center;")
                    .append("'>")

                    .append("Hi <span style='color:#d4a64a;'>")
                    .append(profile.getFullName())
                    .append("</span>,")

                    .append("</h1>")

                    // Divider
                    .append("<div style='")
                    .append("width:100px;")
                    .append("height:1px;")
                    .append("background-color:#d4a64a;")
                    .append("margin:20px auto 30px;")
                    .append("'></div>")

                    // Heading
                    .append("<h2 style='")
                    .append("font-family:Georgia,serif;")
                    .append("font-size:22px;")
                    .append("font-weight:normal;")
                    .append("color:#d4a64a;")
                    .append("margin:0 0 15px;")
                    .append("text-align:center;")
                    .append("'>")
                    .append("Your Daily Expense Summary")
                    .append("</h2>")

                    // Description
                    .append("<p style='")
                    .append("font-size:15px;")
                    .append("line-height:1.7;")
                    .append("color:#bdbdbd;")
                    .append("text-align:center;")
                    .append("margin:0 0 25px;")
                    .append("'>")
                    .append("Here is a summary of your expenses for ")
                    .append(today)
                    .append(".")
                    .append("</p>")

                    // Table
                    .append(table)

                    // Total
                    .append("<div style='")
                    .append("margin-top:25px;")
                    .append("padding:18px;")
                    .append("background-color:#181c21;")
                    .append("border:1px solid #4d3b1d;")
                    .append("border-radius:10px;")
                    .append("text-align:right;")
                    .append("'>")

                    .append("<span style='")
                    .append("font-size:16px;")
                    .append("color:#aaaaaa;")
                    .append("'>")
                    .append("Total Expenses: ")
                    .append("</span>")

                    .append("<span style='")
                    .append("font-size:20px;")
                    .append("font-weight:bold;")
                    .append("color:#d4a64a;")
                    .append("'>")
                    .append("₹")
                    .append(totalExpense)
                    .append("</span>")

                    .append("</div>")

                    // Bottom divider
                    .append("<div style='")
                    .append("width:100px;")
                    .append("height:1px;")
                    .append("background-color:#4d3b1d;")
                    .append("margin:30px auto 25px;")
                    .append("'></div>")

                    // Closing
                    .append("<p style='")
                    .append("font-family:Georgia,serif;")
                    .append("font-size:15px;")
                    .append("line-height:1.7;")
                    .append("color:#d0d0d0;")
                    .append("text-align:center;")
                    .append("margin:0;")
                    .append("'>")

                    .append("Stay consistent with your finances.<br>")
                    .append("<span style='color:#d4a64a;font-weight:bold;'>")
                    .append("Money Manager Team")
                    .append("</span>")

                    .append("</p>")

                    .append("</div>")
                    .append("</div>")

                    .append("</body>")
                    .append("</html>");

            emailService.sendEmail(
                    profile.getEmail(),
                    "Money Manager - Daily Expense Summary",
                    body.toString()
            );
        }

        log.info("Job Completed : SendDailyExpenseSummary()");
    }
}
