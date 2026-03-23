# 🎓 Online Examination System — Java Swing GUI

A fully self-contained desktop application built with **Java Swing**.
No database, no server, no external libraries needed — just Java!

---

## 📸 Screens Overview

| Screen            | Description                                          |
|-------------------|------------------------------------------------------|
| **Login**         | Sign in with email + password                        |
| **Register**      | Create a new student account                         |
| **Student Dashboard** | Browse exams, view past results               |
| **Exam Window**   | Live exam with timer, MCQ options, question navigator|
| **Result Screen** | Score, pass/fail verdict, full answer review         |
| **Admin Dashboard**| Manage exams, students, reports                    |
| **Questions Dialog** | Add/delete questions for an exam                |

---

## 🚀 How to Run

### Prerequisites
- Java 17+ installed
- Check: `java -version` and `javac -version`

### Option 1 — Shell script (Mac/Linux)
```bash
chmod +x run.sh
./run.sh
```

### Option 2 — Batch file (Windows)
```
run.bat
```

### Option 3 — Manual commands
```bash
# 1. Compile
mkdir out
javac -d out -sourcepath src src/com/examgui/Main.java

# 2. Run
java -cp out com.examgui.Main
```

---

## 👥 Demo Accounts

| Role    | Email              | Password   |
|---------|--------------------|------------|
| Admin   | admin@exam.com     | admin123   |
| Student | alice@exam.com     | alice123   |
| Student | bob@exam.com       | bob123     |

---

## 📦 Project Structure

```
exam-gui/
│
├── run.sh                         ← Run script (Mac/Linux)
├── run.bat                        ← Run script (Windows)
│
└── src/com/examgui/
    │
    ├── Main.java                  ← Entry point
    │
    ├── model/                     ← Data classes
    │   ├── User.java              ← Student / Admin
    │   ├── Exam.java              ← Exam with questions list
    │   ├── Question.java          ← MCQ question (A/B/C/D)
    │   └── ExamAttempt.java       ← Student's exam session + score
    │
    ├── data/
    │   └── DataStore.java         ← In-memory database + sample data
    │
    ├── util/
    │   └── UITheme.java           ← All colors, fonts, custom components
    │
    └── ui/                        ← All screens
        ├── LoginFrame.java        ← Login screen
        ├── RegisterFrame.java     ← Registration screen
        ├── StudentDashboard.java  ← Student home (exams + history)
        ├── ExamWindow.java        ← Live exam with timer
        ├── ResultFrame.java       ← Results + answer review
        ├── AdminDashboard.java    ← Admin panel (3 tabs)
        └── QuestionsDialog.java   ← Add/delete questions
```

---

## 🎨 Design

- **Color Scheme**: Deep navy background + electric teal accent + amber/red for alerts
- **Typography**: SansSerif with carefully chosen weights
- **Custom Components**: All buttons, inputs, cards are hand-painted with `Graphics2D`
- **No external libraries**: Pure Java Swing, works on any system with Java 17+

---

## ✨ Features

### Student
- ✅ Register / Login
- ✅ Browse published exams (title, description, duration, pass %)
- ✅ Start an exam (one active attempt per exam)
- ✅ Live countdown timer (turns amber at 5 min, red at 1 min)
- ✅ Question navigator sidebar (green = answered, grey = unanswered)
- ✅ Skip and return to questions
- ✅ Submit with confirmation (warns about unanswered questions)
- ✅ Auto-submit on timeout
- ✅ Full answer review after submission (correct/incorrect per question)
- ✅ Score, percentage, pass/fail result screen
- ✅ Exam history tab

### Admin
- ✅ Create exams (title, description, duration, passing score)
- ✅ Add MCQ questions to any exam (A/B/C/D + correct answer + marks)
- ✅ Delete questions (right-click in table)
- ✅ Publish / Unpublish exams
- ✅ Delete exams
- ✅ View all students with attempt count and average score
- ✅ Reports tab: summary cards + all attempts table with pass/fail

---

## 💡 Key Java Concepts Used

| Concept                | Where                         |
|------------------------|-------------------------------|
| `JFrame` / `JDialog`   | All screens                   |
| `JPanel` + layouts     | Every screen layout           |
| `Graphics2D`           | Custom button/card rendering  |
| `BoxLayout` / `BorderLayout` | Screen composition      |
| `JTable` + custom renderer | Admin tables             |
| `JRadioButton` group   | MCQ answer selection          |
| `javax.swing.Timer`    | Countdown timer               |
| `JTabbedPane`          | Dashboard tabs                |
| `Singleton pattern`    | DataStore                     |
| `OOP + Encapsulation`  | Model classes                 |
| `Lambda expressions`   | Event listeners               |
| `Stream API`           | Filtering/mapping data        |

---

## 🔧 Extending the Project

### Add file persistence (save data between runs)
Use `ObjectOutputStream` / `ObjectInputStream` to serialize `DataStore` to a `.dat` file.

### Switch to a real database
Replace `DataStore` with JDBC + SQLite:
```xml
<!-- Add SQLite JDBC driver to classpath -->
org.xerial:sqlite-jdbc:3.43.0
```

### Add more question types
Extend `Question` with a `QuestionType` enum: `MCQ`, `TRUE_FALSE`, `SHORT_ANSWER`.
