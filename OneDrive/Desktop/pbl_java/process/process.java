import java.util.*;

class User {
    int id;
    String name, email, password;
    boolean isAdmin;

    static int count = 1;

    User(String name, String email, String password, boolean isAdmin) {
        this.id = count++;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }
}

class Question {
    String text;
    String[] options;
    char correct;
    int marks;

    Question(String text, String a, String b, String c, String d, char correct, int marks) {
        this.text = text;
        this.options = new String[]{a, b, c, d};
        this.correct = correct;
        this.marks = marks;
    }
}

class Exam {
    int id;
    String title;
    int duration;
    int passPercent;
    boolean published = true;

    List<Question> questions = new ArrayList<>();
    static int count = 1;

    Exam(String title, int duration, int passPercent) {
        this.id = count++;
        this.title = title;
        this.duration = duration;
        this.passPercent = passPercent;
    }
}

class DataStore {
    static List<User> users = new ArrayList<>();
    static List<Exam> exams = new ArrayList<>();

    static {
        // Users
        users.add(new User("Admin", "admin@exam.com", "admin123", true));
        users.add(new User("Pulkit", "pulkit@gmail.com", "123", false));

        // Exam
        Exam e = new Exam("Java Basics", 30, 50);

        e.questions.add(new Question(
                "Java is?",
                "Language", "OS", "Browser", "IDE",
                'A', 1));

        e.questions.add(new Question(
                "Keyword for class?",
                "class", "define", "struct", "object",
                'A', 1));

        exams.add(e);
    }
}

public class process {

    static Scanner sc = new Scanner(System.in);
    static User currentUser;

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n=== ONLINE EXAM SYSTEM ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> login();
                case 2 -> register();
                case 3 -> System.exit(0);
            }
        }
    }

    static void login() {
        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        for (User u : DataStore.users) {
            if (u.email.equals(email) && u.password.equals(pass)) {
                currentUser = u;
                System.out.println("Login Successful!");

                if (u.isAdmin) adminMenu();
                else studentMenu();

                return;
            }
        }
        System.out.println("Invalid credentials!");
    }

    static void register() {
        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        DataStore.users.add(new User(name, email, pass, false));
        System.out.println("Registered Successfully!");
    }

    // ================= STUDENT =================

    static void studentMenu() {
        while (true) {
            System.out.println("\n--- STUDENT MENU ---");
            System.out.println("1. View Exams");
            System.out.println("2. Logout");

            int ch = sc.nextInt();

            switch (ch) {
                case 1 -> showExams();
                case 2 -> {
                    currentUser = null;
                    return;
                }
            }
        }
    }

    static void showExams() {
        for (Exam e : DataStore.exams) {
            System.out.println(e.id + ". " + e.title);
        }

        System.out.print("Enter exam id: ");
        int id = sc.nextInt();

        for (Exam e : DataStore.exams) {
            if (e.id == id) {
                takeExam(e);
                return;
            }
        }
    }

    static void takeExam(Exam exam) {
        int score = 0;
        int total = 0;

        System.out.println("\nStarting Exam: " + exam.title);

        for (Question q : exam.questions) {
            System.out.println("\n" + q.text);
            char option = 'A';

            for (String op : q.options) {
                System.out.println(option++ + ". " + op);
            }

            System.out.print("Answer: ");
            char ans = sc.next().toUpperCase().charAt(0);

            total += q.marks;

            if (ans == q.correct) {
                score += q.marks;
            }
        }

        double percent = (score * 100.0) / total;
        boolean pass = percent >= exam.passPercent;

        System.out.println("\n=== RESULT ===");
        System.out.println("Score: " + score + "/" + total);
        System.out.println("Percentage: " + percent + "%");
        System.out.println(pass ? "PASS" : "FAIL");
    }

    // ================= ADMIN =================

    static void adminMenu() {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Create Exam");
            System.out.println("2. View Exams");
            System.out.println("3. Logout");

            int ch = sc.nextInt();

            switch (ch) {
                case 1 -> createExam();
                case 2 -> viewExamsAdmin();
                case 3 -> {
                    currentUser = null;
                    return;
                }
            }
        }
    }

    static void createExam() {
        sc.nextLine();

        System.out.print("Exam Title: ");
        String title = sc.nextLine();

        Exam exam = new Exam(title, 30, 50);

        System.out.print("Number of Questions: ");
        int n = sc.nextInt();
        sc.nextLine();

        for (int i = 0; i < n; i++) {
            System.out.println("\nQuestion " + (i + 1));

            System.out.print("Text: ");
            String text = sc.nextLine();

            System.out.print("A: ");
            String a = sc.nextLine();

            System.out.print("B: ");
            String b = sc.nextLine();

            System.out.print("C: ");
            String c = sc.nextLine();

            System.out.print("D: ");
            String d = sc.nextLine();

            System.out.print("Correct (A/B/C/D): ");
            char correct = sc.next().toUpperCase().charAt(0);

            exam.questions.add(new Question(text, a, b, c, d, correct, 1));
            sc.nextLine();
        }

        DataStore.exams.add(exam);
        System.out.println("Exam Created!");
    }

    static void viewExamsAdmin() {
        for (Exam e : DataStore.exams) {
            System.out.println(e.id + ". " + e.title + " (Questions: " + e.questions.size() + ")");
        }
    }
}