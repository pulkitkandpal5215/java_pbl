# 🎓 Online Examination System — JavaFX

A fully self-contained desktop application built with **JavaFX**.
No database, no server — just Java + JavaFX!

---

## 📸 Screens

| Screen                  | Description                                            |
|-------------------------|--------------------------------------------------------|
| **Login**               | Sign in with email + password                          |
| **Register**            | Create a new student account                           |
| **Student Dashboard**   | Browse exams, view past results (tabbed layout)        |
| **Exam Screen**         | Live exam with countdown timer + sidebar navigator     |
| **Result Screen**       | Score, pass/fail verdict, full answer review           |
| **Admin Dashboard**     | Manage exams, students, reports (3 tabs)               |
| **Questions Dialog**    | Add/delete questions for any exam                      |

---

## 🚀 How to Run

### Prerequisites
- **Java 17+** — check with `java -version`
- **JavaFX SDK 17+** — download from https://gluonhq.com/products/javafx/

### Setup (one-time)

1. Download the JavaFX SDK for your OS from https://gluonhq.com/products/javafx/
2. Extract it somewhere, e.g. `~/javafx-sdk-21` or `C:\javafx-sdk-21`
3. Set the environment variable (or edit the run script directly):

```bash
# Linux / macOS
export JAVAFX_PATH=/path/to/javafx-sdk-21/lib

# Windows
set JAVAFX_PATH=C:\path\to\javafx-sdk-21\lib
```

### Option A — Shell script (Linux / macOS)
```bash
chmod +x run.sh
./run.sh
```

### Option B — Batch file (Windows)
```
run.bat
```

### Option C — Manual
```bash
# Compile
mkdir out
find src -name "*.java" > sources.txt
javac --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml -d out @sources.txt

# Run
java --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml -cp out com.examgui.Main
```

### Ubuntu / Debian shortcut
```bash
sudo apt install openjfx
export JAVAFX_PATH=/usr/share/openjfx/lib
./run.sh
```

---

## 👥 Demo Accounts

| Role    | Email                  | Password    |
|---------|------------------------|-------------|
| Admin   | admin@exam.com         | admin123    |
| Student | pulkit@gmail.com       | pulkit123   |
| Student | vaibhav@gmail.com      | vaibhav123  |

---

## 📦 Project Structure

```
examfx/
│
├── run.sh                              ← Build & run (Linux/macOS)
├── run.bat                             ← Build & run (Windows)
│
└── src/com/examgui/
    │
    ├── Main.java                       ← JavaFX Application entry point
    │
    ├── model/                          ← Data classes (unchanged from Swing)
    │   ├── User.java
    │   ├── Exam.java
    │   ├── Question.java
    │   └── ExamAttempt.java
    │
    ├── data/
    │   └── DataStore.java              ← In-memory singleton database
    │
    ├── util/
    │   └── UITheme.java                ← Colors, CSS, factory methods
    │
    └── ui/                             ← All screens (Scene-based)
        ├── LoginScreen.java
        ├── RegisterScreen.java
        ├── StudentDashboardScreen.java
        ├── ExamScreen.java
        ├── ResultScreen.java
        └── AdminDashboardScreen.java   ← includes Questions dialog
```

---

## 🔄 Swing → JavaFX Migration Map

| Swing                        | JavaFX Equivalent                              |
|------------------------------|------------------------------------------------|
| `JFrame`                     | `Stage` + `Scene`                              |
| `JPanel` (BorderLayout)      | `BorderPane`                                   |
| `JPanel` (BoxLayout Y)       | `VBox`                                         |
| `JPanel` (FlowLayout)        | `HBox` / `FlowPane`                            |
| `JLabel`                     | `Label`                                        |
| `JTextField`                 | `TextField`                                    |
| `JPasswordField`             | `PasswordField`                                |
| `JButton`                    | `Button`                                       |
| `JRadioButton` + ButtonGroup | `RadioButton` + `ToggleGroup`                  |
| `JScrollPane`                | `ScrollPane`                                   |
| `JTabbedPane`                | `TabPane` + `Tab`                              |
| `JTable` + `DefaultTableModel` | `TableView` + `TableColumn` + `ObservableList` |
| `JDialog`                    | `Stage` (with `initOwner`)                     |
| `JOptionPane`                | `Alert`                                        |
| `javax.swing.Timer`          | `javafx.animation.Timeline`                    |
| `Graphics2D` custom painting | CSS `-fx-*` properties + inline styles         |
| `UIManager.put(...)`         | Global CSS stylesheet on `Scene`               |
| `SwingUtilities.invokeLater` | `Platform.runLater`                            |
| `Hyperlink` (simulated)      | `Hyperlink`                                    |

---

## 🎨 Design

- **Same color palette** as the Swing version: deep navy (`#0F1923`) + electric teal (`#00C9A7`) + amber/red alerts
- **Styling via CSS** — all colors, radii, and hover effects defined in `UITheme.GLOBAL_CSS` and per-node inline styles
- **No external libraries** — pure JavaFX, works with any Java 17+ + JavaFX 17+ setup

---

## ✨ Features

### Student
- ✅ Register / Login
- ✅ Browse published exams
- ✅ Start an exam (one active attempt per exam enforced)
- ✅ Live countdown timer (amber at 5 min, red at 1 min)
- ✅ Question sidebar navigator (teal = current, green = answered, grey = unanswered)
- ✅ Skip and return to questions freely
- ✅ Submit with confirmation (warns about unanswered questions)
- ✅ Auto-submit on timeout
- ✅ Full answer review after submission
- ✅ Score, percentage, pass/fail result screen
- ✅ Exam history tab

### Admin
- ✅ Create exams (title, description, duration, passing score)
- ✅ Add MCQ questions (A/B/C/D, correct answer, marks)
- ✅ Delete questions
- ✅ Publish / Unpublish exams
- ✅ Delete exams
- ✅ View all students with attempt count and average score
- ✅ Reports tab: summary cards + all attempts table

---

## 💡 Key JavaFX Concepts Used

| Concept                     | Where Used                                    |
|-----------------------------|-----------------------------------------------|
| `Application.start(Stage)`  | `Main.java` entry point                       |
| `Scene` + `Stage`           | All screen transitions                        |
| `BorderPane` / `VBox` / `HBox` | All layouts                                |
| `TabPane` + `Tab`           | Student & Admin dashboards                    |
| `TableView` + `TableColumn` | Admin exam/student/reports tables             |
| `ToggleGroup` + `RadioButton` | MCQ option selection in `ExamScreen`        |
| `Timeline` (animation)      | Countdown timer in `ExamScreen`               |
| `Alert`                     | Confirmations, errors, info dialogs           |
| `ScrollPane`                | Scrollable exam list, result review           |
| CSS inline styles           | All component theming via `UITheme`           |
| `ObservableList`            | TableView data binding                        |
| `SimpleStringProperty`      | TableColumn cell value factories              |
| `Platform.runLater`         | Safe UI updates from Timeline callbacks       |
| Lambda event handlers       | All button/action listeners                   |
