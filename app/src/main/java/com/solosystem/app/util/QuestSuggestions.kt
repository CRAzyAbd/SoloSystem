package com.solosystem.app.util

import com.solosystem.app.data.model.*

data class QuestSuggestion(
    val name: String,
    val description: String,
    val tier: QuestTier,
    val stat: StatType,
    val type: QuestType,
    val estimatedMinutes: Int
)

object QuestSuggestions {
    val all: List<QuestSuggestion> = listOf(
        // ── DISCIPLINE (15) ──────────────────────────────────────────────────
        QuestSuggestion("Wake Up on Time",        "Hit your target wake time",                QuestTier.D, StatType.DISCIPLINE, QuestType.DAILY,  5),
        QuestSuggestion("No Phone Before Bed",    "No screen 30min before sleep",             QuestTier.D, StatType.DISCIPLINE, QuestType.DAILY,  30),
        QuestSuggestion("Make Your Bed",          "Start the day with a win",                 QuestTier.E, StatType.DISCIPLINE, QuestType.DAILY,  5),
        QuestSuggestion("Plan Tomorrow Tonight",  "Write 3 priorities for tomorrow",          QuestTier.D, StatType.DISCIPLINE, QuestType.DAILY,  15),
        QuestSuggestion("Digital Detox Hour",     "1 hour with no social media",              QuestTier.C, StatType.DISCIPLINE, QuestType.DAILY,  60),
        QuestSuggestion("Cold Shower",            "End your shower with 30s cold water",      QuestTier.C, StatType.DISCIPLINE, QuestType.DAILY,  10),
        QuestSuggestion("Sleep Before Midnight",  "In bed before 12am",                       QuestTier.D, StatType.DISCIPLINE, QuestType.DAILY,  5),
        QuestSuggestion("7-Day Streak",           "Complete all daily quests 7 days straight",QuestTier.B, StatType.DISCIPLINE, QuestType.WEEKLY, 0),
        QuestSuggestion("Zero Procrastination Day","Start every task within 2 min of deciding",QuestTier.B,StatType.DISCIPLINE, QuestType.DAILY,  0),
        QuestSuggestion("Weekly Review",          "Review your week, plan the next",          QuestTier.C, StatType.DISCIPLINE, QuestType.WEEKLY, 30),
        QuestSuggestion("No Snooze Week",         "No snooze button for 7 days",              QuestTier.B, StatType.DISCIPLINE, QuestType.WEEKLY, 0),
        QuestSuggestion("Journaling",             "Write 5 minutes in a journal",             QuestTier.E, StatType.DISCIPLINE, QuestType.DAILY,  10),
        QuestSuggestion("Evening Routine",        "Complete your full evening routine",        QuestTier.D, StatType.DISCIPLINE, QuestType.DAILY,  20),
        QuestSuggestion("Morning Routine",        "Complete your full morning routine",        QuestTier.D, StatType.DISCIPLINE, QuestType.DAILY,  30),
        QuestSuggestion("Single-Task Focus",      "Work on one thing for 90 min, no switching",QuestTier.B,StatType.DISCIPLINE, QuestType.DAILY,  90),

        // ── ACADEMIC (15) ────────────────────────────────────────────────────
        QuestSuggestion("Study Session 30min",    "Focused study, no distractions",           QuestTier.C, StatType.ACADEMIC,  QuestType.DAILY,  30),
        QuestSuggestion("Study Session 1hr",      "Deep work study block",                    QuestTier.B, StatType.ACADEMIC,  QuestType.DAILY,  60),
        QuestSuggestion("Study Session 2hr",      "Extended deep work block",                 QuestTier.A, StatType.ACADEMIC,  QuestType.DAILY,  120),
        QuestSuggestion("Read 20 Pages",          "Read 20 pages of any book",                QuestTier.C, StatType.ACADEMIC,  QuestType.DAILY,  30),
        QuestSuggestion("Complete Assignment",    "Finish and submit one assignment",          QuestTier.B, StatType.ACADEMIC,  QuestType.DAILY,  90),
        QuestSuggestion("Revise One Chapter",     "Full chapter revision with notes",         QuestTier.B, StatType.ACADEMIC,  QuestType.DAILY,  60),
        QuestSuggestion("Solve 10 Problems",      "Math/coding/logic problems",               QuestTier.C, StatType.ACADEMIC,  QuestType.DAILY,  45),
        QuestSuggestion("Watch a Lecture",        "Watch one full lecture and take notes",    QuestTier.C, StatType.ACADEMIC,  QuestType.DAILY,  60),
        QuestSuggestion("Flashcard Review",       "Review 50 flashcards",                     QuestTier.D, StatType.ACADEMIC,  QuestType.DAILY,  20),
        QuestSuggestion("Study Group Session",    "2hr session with classmates",              QuestTier.B, StatType.ACADEMIC,  QuestType.WEEKLY, 120),
        QuestSuggestion("Past Paper Practice",    "Complete one past exam paper",             QuestTier.A, StatType.ACADEMIC,  QuestType.WEEKLY, 120),
        QuestSuggestion("Write 500 Words",        "Essay, notes, or blog — any topic",        QuestTier.C, StatType.ACADEMIC,  QuestType.DAILY,  40),
        QuestSuggestion("Learn Something New",    "Spend 30min learning outside syllabus",    QuestTier.C, StatType.ACADEMIC,  QuestType.DAILY,  30),
        QuestSuggestion("Weekly Study Goal",      "Hit your target study hours this week",    QuestTier.A, StatType.ACADEMIC,  QuestType.WEEKLY, 0),
        QuestSuggestion("Teach Someone",          "Explain a concept to a classmate",         QuestTier.C, StatType.ACADEMIC,  QuestType.WEEKLY, 30),

        // ── PHYSICAL (15) ────────────────────────────────────────────────────
        QuestSuggestion("Gym Session",            "Full workout at the gym",                  QuestTier.B, StatType.PHYSICAL,  QuestType.DAILY,  90),
        QuestSuggestion("Morning Run",            "Run at least 3km",                         QuestTier.C, StatType.PHYSICAL,  QuestType.DAILY,  30),
        QuestSuggestion("10,000 Steps",           "Hit your daily step goal",                 QuestTier.C, StatType.PHYSICAL,  QuestType.DAILY,  0),
        QuestSuggestion("Home Workout",           "30min bodyweight workout",                 QuestTier.C, StatType.PHYSICAL,  QuestType.DAILY,  30),
        QuestSuggestion("Push-Up Challenge",      "100 push-ups throughout the day",          QuestTier.C, StatType.PHYSICAL,  QuestType.DAILY,  30),
        QuestSuggestion("Stretching Session",     "15min full-body stretching",               QuestTier.D, StatType.PHYSICAL,  QuestType.DAILY,  15),
        QuestSuggestion("Cycle 10km",             "Cycling session — at least 10km",          QuestTier.C, StatType.PHYSICAL,  QuestType.DAILY,  40),
        QuestSuggestion("Swim Session",           "30min swim",                               QuestTier.C, StatType.PHYSICAL,  QuestType.WEEKLY, 30),
        QuestSuggestion("Beat a Personal Record", "Hit a new PR on any exercise",             QuestTier.A, StatType.PHYSICAL,  QuestType.WEEKLY, 90),
        QuestSuggestion("Active Rest Day",        "Walk or light activity on rest day",       QuestTier.D, StatType.PHYSICAL,  QuestType.DAILY,  20),
        QuestSuggestion("Sport Practice",         "1hr of any sport",                         QuestTier.C, StatType.PHYSICAL,  QuestType.WEEKLY, 60),
        QuestSuggestion("Plank 5 Minutes",        "Hold plank for 5 total minutes",           QuestTier.C, StatType.PHYSICAL,  QuestType.DAILY,  10),
        QuestSuggestion("No Elevator Week",       "Stairs only for 7 days",                   QuestTier.D, StatType.PHYSICAL,  QuestType.WEEKLY, 0),
        QuestSuggestion("Yoga Session",           "30min yoga flow",                          QuestTier.C, StatType.PHYSICAL,  QuestType.WEEKLY, 30),
        QuestSuggestion("5km Run",                "Complete a 5km run without stopping",      QuestTier.B, StatType.PHYSICAL,  QuestType.WEEKLY, 35),

        // ── WELLNESS (10) ────────────────────────────────────────────────────
        QuestSuggestion("Drink 2L Water",         "Hit your daily hydration target",          QuestTier.E, StatType.WELLNESS,  QuestType.DAILY,  0),
        QuestSuggestion("Eat Clean Today",        "No junk food or fast food",                QuestTier.C, StatType.WELLNESS,  QuestType.DAILY,  0),
        QuestSuggestion("Sleep 7+ Hours",         "Get at least 7 hours of sleep",            QuestTier.C, StatType.WELLNESS,  QuestType.DAILY,  0),
        QuestSuggestion("Meditation 10min",       "Guided or silent meditation",              QuestTier.D, StatType.WELLNESS,  QuestType.DAILY,  10),
        QuestSuggestion("No Caffeine Day",        "Skip coffee/energy drinks today",          QuestTier.C, StatType.WELLNESS,  QuestType.WEEKLY, 0),
        QuestSuggestion("Gratitude Log",          "Write 3 things you're grateful for",       QuestTier.E, StatType.WELLNESS,  QuestType.DAILY,  5),
        QuestSuggestion("Screen Time Under 2hr",  "Total recreational screen time under 2hr", QuestTier.B, StatType.WELLNESS,  QuestType.DAILY,  0),
        QuestSuggestion("Cook a Healthy Meal",    "Prepare a nutritious home-cooked meal",    QuestTier.C, StatType.WELLNESS,  QuestType.WEEKLY, 45),
        QuestSuggestion("Sunlight 15min",         "Spend 15min outside in natural light",     QuestTier.D, StatType.WELLNESS,  QuestType.DAILY,  15),
        QuestSuggestion("Digital Sunset",         "No screens after 9pm for a week",          QuestTier.B, StatType.WELLNESS,  QuestType.WEEKLY, 0),

        // ── SKILLS (10) ──────────────────────────────────────────────────────
        QuestSuggestion("Code for 1 Hour",        "Work on a personal coding project",        QuestTier.B, StatType.SKILLS,   QuestType.DAILY,  60),
        QuestSuggestion("LeetCode Problem",       "Solve one algorithm challenge",            QuestTier.C, StatType.SKILLS,   QuestType.DAILY,  45),
        QuestSuggestion("Learn a New Command",    "Learn and practice 5 new terminal commands",QuestTier.D,StatType.SKILLS,   QuestType.DAILY,  20),
        QuestSuggestion("Build a Feature",        "Implement one feature in your project",    QuestTier.A, StatType.SKILLS,   QuestType.WEEKLY, 180),
        QuestSuggestion("Watch a Tech Tutorial",  "Complete one technical tutorial video",    QuestTier.C, StatType.SKILLS,   QuestType.DAILY,  45),
        QuestSuggestion("CTF Challenge",          "Attempt a Capture the Flag challenge",     QuestTier.B, StatType.SKILLS,   QuestType.WEEKLY, 120),
        QuestSuggestion("Read Tech Documentation","Read official docs for a tool you use",    QuestTier.C, StatType.SKILLS,   QuestType.DAILY,  30),
        QuestSuggestion("GitHub Contribution",    "Make a meaningful commit to a project",    QuestTier.C, StatType.SKILLS,   QuestType.DAILY,  60),
        QuestSuggestion("Learn Networking Concept","Study one networking/security concept",   QuestTier.C, StatType.SKILLS,   QuestType.DAILY,  30),
        QuestSuggestion("Weekly Project Progress","Make measurable progress on your project", QuestTier.A, StatType.SKILLS,   QuestType.WEEKLY, 180)
    )

    fun getByStats(stats: List<StatType>): List<QuestSuggestion> =
        all.filter { it.stat in stats }

    fun getByType(type: QuestType): List<QuestSuggestion> =
        all.filter { it.type == type }
}