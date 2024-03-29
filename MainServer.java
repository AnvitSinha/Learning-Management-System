import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Project 5 - MainServer
 * <p>
 * Handles all requests from the user by invoking the relevant methods in their respective classes. This class runs
 * using a while (true) loop, which keeps it running until it is closed manually. This class also has all the methods
 * surrounded by synchronized blocks to make all threads work concurrently.
 *
 * @author Group 66, L16
 * @version May 2, 2022
 */

public class MainServer implements Runnable {

    private static final Object GATEKEEPER = new Object();  // used to synchronize all users
    private final Socket client;

    public MainServer(Socket client) {

        this.client = client;

    }

    public void run() {

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
            String currentLine;

            // Initialize all classes
            Account.initializeAccounts();
            Course.initializeCourses();
            GradeBook.initializeStudentGradebook();
            QuizFile q = new QuizFile("quizDatabase.txt");

            while (true) {      // Server keeps running and is ready to accept the user's input

                currentLine = reader.readLine();

                if (currentLine != null) {

                    switch (currentLine) {

                        // Account options
                        case "verify" -> {

                            synchronized (GATEKEEPER) {

                                String username = reader.readLine();
                                String password = reader.readLine();

                                String response = String.valueOf(Account.isValidCredential(username, password));

                                writer.println(response);
                                writer.flush();

                            }


                        }

                        case "updateAccount" -> {

                            synchronized (GATEKEEPER) {

                                String username = reader.readLine();
                                String password = reader.readLine();
                                Account account = new Account(username, password);

                                int choice = Integer.parseInt(reader.readLine());   // get user's choice for what to update

                                switch (choice) {

                                    case 0 -> {

                                        String newName = reader.readLine();

                                        account.updateName(newName);

                                        writer.println("true");
                                        writer.flush();

                                    }       // update name

                                    case 1 -> {

                                        String newUsername = reader.readLine();

                                        String response = String.valueOf(account.updateUsername(newUsername));

                                        writer.println(response);
                                        writer.flush();

                                    }       // Update username

                                    case 2 -> {

                                        String newPassword = reader.readLine();

                                        String response = String.valueOf(account.updatePassword(newPassword));

                                        writer.println(response);
                                        writer.flush();

                                    }       // update password


                                }

                            }

                        }

                        case "deleteAccount" -> {

                            synchronized (GATEKEEPER) {
                                String username = reader.readLine();
                                String password = reader.readLine();
                                Account account = new Account(username, password);

                                String response = String.valueOf(account.deleteAccount());

                                writer.println(response);
                                writer.flush();
                            }

                        }

                        case "newAccount" -> {

                            synchronized (GATEKEEPER) {
                                String type = reader.readLine();
                                String name = reader.readLine();
                                String username = reader.readLine();
                                String password = reader.readLine();

                                String response = String.valueOf(Account.addNewAccount(name, username, password, type));

                                writer.println(response);
                                writer.flush();
                            }


                        }

                        case "accountDetails" -> {

                            synchronized (GATEKEEPER) {
                                String username = reader.readLine();
                                String password = reader.readLine();

                                Account temp = new Account(username, password);

                                writer.println(temp);
                                writer.flush();
                            }

                        }

                        // Quiz Options
                        case "printQuiz" -> {
                            synchronized (GATEKEEPER) {
                                q.readFile();
                                String quizName = reader.readLine();
                                String type = reader.readLine();
                                Quiz myQuiz = q.getQuiz(quizName);
                                writer.write(myQuiz.stringify(type));
                                writer.println();
                                writer.flush();
                            }
                        }

                        case "addQuiz" -> {

                            synchronized (GATEKEEPER) {
                                String courseName = reader.readLine();
                                String quizName = reader.readLine();

                                writer.write(String.valueOf(q.addQuiz(courseName, quizName)));
                                writer.println();
                                writer.flush();
                            }
                        }

                        case "addQuestion" -> {
                            synchronized (GATEKEEPER) {
                                String quizName = reader.readLine();
                                int type = Integer.parseInt(reader.readLine());
                                String question = reader.readLine();
                                ArrayList<String> answers =
                                        new ArrayList<String>(Arrays.asList(reader.readLine().split("&")));
                                String correctAnswer = reader.readLine();

                                writer.write(String.valueOf(q.addQuestion(quizName, type, question, answers, correctAnswer)));
                                writer.println();
                                writer.flush();
                            }
                        }

                        case "deleteQuiz" -> {
                            synchronized (GATEKEEPER) {
                                String quizName = reader.readLine();

                                writer.write(String.valueOf(q.deleteQuiz(quizName)));
                                writer.println();
                                writer.flush();
                            }
                        }

                        case "deleteQuestion" -> {
                            synchronized (GATEKEEPER) {
                                String quizName = reader.readLine();
                                int questionNum = Integer.parseInt(reader.readLine());

                                writer.write(String.valueOf(q.deleteQuestion(quizName, questionNum)));
                                writer.println();
                                writer.flush();
                            }
                        }

                        case "editQuizName" -> {
                            synchronized (GATEKEEPER) {
                                String quizName = reader.readLine();
                                String newName = reader.readLine();

                                writer.write(String.valueOf(q.editQuizName(quizName, newName)));
                                writer.println();
                                writer.flush();
                            }
                        }

                        case "editQuizCourse" -> {
                            synchronized (GATEKEEPER) {
                                String quizName = reader.readLine();
                                String newCourse = reader.readLine();

                                writer.write(String.valueOf(q.editQuizCourse(quizName, newCourse)));
                                writer.println();
                                writer.flush();
                            }
                        }

                        case "editQuestion" -> {

                            synchronized (GATEKEEPER) {

                                q.readFile();

                                String quizName = reader.readLine();
                                int questionNumber = Integer.parseInt(reader.readLine());
                                int type = Integer.parseInt(reader.readLine());
                                String question = reader.readLine();
                                ArrayList<String> answers =
                                        new ArrayList<String>(Arrays.asList(reader.readLine().split("&")));
                                String correctAnswer = reader.readLine();

                                writer.write(String.valueOf(q.editQuestion(quizName, questionNumber,
                                        type, question, answers, correctAnswer)));
                                writer.println();
                                writer.flush();
                            }
                        }

                        case "getCourseQuizzes" -> {
                            synchronized (GATEKEEPER) {

                                q.readFile();

                                String courseName = reader.readLine();
                                String type = reader.readLine();
                                ArrayList<Quiz> myQuiz = q.getCourseQuizzes(courseName);

                                StringBuilder sb = new StringBuilder();

                                for (Quiz temp : myQuiz) {

                                    sb.append(temp.stringify(type));
                                }

                                writer.write(sb.toString());
                                writer.println();
                                writer.flush();

                            }
                        }

                        case "listQuizNames" -> {

                            synchronized (GATEKEEPER) {
                                q.readFile();

                                String courseName = reader.readLine();
                                ArrayList<String> myQuizName = q.getCourseQuizName(courseName);

                                writer.println(myQuizName.size());

                                for (String s : myQuizName) {

                                    writer.println(s);

                                }
                            }

                        }

                        case "addQuizFile" -> {

                            synchronized (GATEKEEPER) {
                                String quizFile = reader.readLine();

                                writer.println(q.addQuizFile(quizFile));
                            }


                        }

                        case "randomizeQuiz" -> {

                            synchronized (GATEKEEPER) {
                                q.readFile();
                                String quizName = reader.readLine();
                                String type = reader.readLine();
                                Quiz myQuiz = q.randomizeQuestions(quizName);
                                writer.write(myQuiz.stringify(type));
                                writer.println();
                                writer.flush();
                                writer.println(myQuiz.getQuestions().size());
                                writer.flush();
                            }

                        }

                        case "correctAns" -> {

                            synchronized (GATEKEEPER) {
                                String file = reader.readLine();
                                int correct = q.numCorrectAns(file);
                                writer.println(correct);
                            }

                        }

                        // Course options
                        case "addCourse" -> {

                            synchronized (GATEKEEPER) {
                                String courseName = reader.readLine();

                                String response = String.valueOf(Course.addCourse(courseName));

                                writer.println(response);
                                writer.flush();
                            }

                        }

                        case "listCourses" -> {

                            synchronized (GATEKEEPER) {

                                Course.initializeCourses();

                                ArrayList<String> allCourses = Course.listCourses();

                                writer.println(allCourses.size());
                                writer.flush();

                                for (int i = 0; i < allCourses.size(); i++) {

                                    writer.println(String.format("Course%d: %s", (i + 1), allCourses.get(i)));
                                    writer.flush();

                                }
                            }

                        }

                        case "deleteCourse" -> {

                            synchronized (GATEKEEPER) {

                                String courseName = reader.readLine();

                                String response = String.valueOf(Course.deleteCourse(courseName));

                                writer.println(response);
                                writer.flush();

                            }

                        }

                        case "courseDetails" -> {

                            synchronized (GATEKEEPER) {
                                Course.initializeCourses();

                                String courseName = reader.readLine();

                                Course temp = new Course(courseName, q);

                                writer.println(temp);
                                writer.flush();
                            }

                        }

                        //GradeBook Options
                        case "courseAverage" -> {

                            synchronized (GATEKEEPER) {
                                GradeBook.initializeStudentGradebook();

                                String course = reader.readLine();

                                String response = String.valueOf(GradeBook.calculateCourseAverage(course));

                                writer.println(response);
                                writer.flush();
                            }

                        }

                        case "studentAverage" -> {

                            synchronized (GATEKEEPER) {

                                GradeBook.initializeStudentGradebook();
                                String student = reader.readLine();

                                String response = String.valueOf(GradeBook.calculateStudentAverage(student));

                                writer.println(response);
                                writer.flush();
                            }

                        }

                        case "studentInCourse" -> {

                            synchronized (GATEKEEPER) {
                                GradeBook.initializeStudentGradebook();
                                String student = reader.readLine();
                                String course = reader.readLine();

                                String response = String.valueOf(GradeBook.studentCourseGrade(student, course));

                                writer.println(response);
                                writer.flush();
                            }

                        }

                        case "quizSubmissions" -> {

                            synchronized (GATEKEEPER) {
                                GradeBook.initializeStudentGradebook();

                                String quiz = reader.readLine();
                                ArrayList<String> submissions = GradeBook.viewQuizSubmissions(quiz);

                                writer.println(submissions.size());
                                writer.flush();

                                for (String submission : submissions) {

                                    writer.write(submission);
                                    writer.println();
                                    writer.flush();

                                }
                            }

                        }

                        case "quizAverage" -> {

                            synchronized (GATEKEEPER) {
                                GradeBook.initializeStudentGradebook();
                                String quizName = reader.readLine();

                                double average = GradeBook.calculateQuizAverage(quizName);

                                writer.println(average);
                            }

                        }

                        case "addSubmission" -> {

                            synchronized (GATEKEEPER) {

                                String name = reader.readLine();
                                String quiz = reader.readLine();
                                double score = Double.parseDouble(reader.readLine());
                                String course = reader.readLine();

                                writer.println(GradeBook.addNewSubmission(name, quiz, score, course));
                            }

                        }

                    }

                }
            }


        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An Error Occurred At Server!",
                    "Server Error", JOptionPane.WARNING_MESSAGE);
        }

    }
}
