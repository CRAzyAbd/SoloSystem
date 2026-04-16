package com.solosystem.app.util

import com.solosystem.app.data.model.UserProfile

data class TitleDef(val id: String, val name: String, val description: String)

object TitleChecker {
    val allTitles = listOf(
        TitleDef("early_riser",       "Early Riser",          "Wake on time 7 days straight"),
        TitleDef("iron_will",         "Iron Will",            "30-day quest completion streak"),
        TitleDef("the_grind",         "The Grind",            "Complete 100 quests total"),
        TitleDef("focused_mind",      "Focused Mind",         "Log 50 total hours in Focus Mode"),
        TitleDef("untouchable",       "Untouchable",          "No penalty triggered in 60 days"),
        TitleDef("d_rank",            "D-Rank Hunter",        "Reached D Rank"),
        TitleDef("c_rank",            "C-Rank Hunter",        "Reached C Rank"),
        TitleDef("b_rank",            "B-Rank Hunter",        "Reached B Rank"),
        TitleDef("a_rank",            "A-Rank Hunter",        "Reached A Rank"),
        TitleDef("s_rank",            "S-Rank Hunter",        "Reached S Rank — Elite"),
        TitleDef("centurion",         "Centurion",            "Complete 100 quests"),
        TitleDef("returned_from_abyss","Returned from Abyss", "Reached A-Rank after a Level 1 reset")
    )

    fun checkUnlocks(profile: UserProfile): List<TitleDef> {
        val unlocked = mutableListOf<TitleDef>()
        if (profile.streak >= 30)                        unlocked += allTitles.first { it.id == "iron_will" }
        if (profile.totalQuestsDone >= 100)              unlocked += allTitles.first { it.id == "centurion" }
        if (profile.totalFocusMinutes >= 3000)           unlocked += allTitles.first { it.id == "focused_mind" }
        if (profile.rank.ordinal >= 1)                   unlocked += allTitles.first { it.id == "d_rank" }
        if (profile.rank.ordinal >= 2)                   unlocked += allTitles.first { it.id == "c_rank" }
        if (profile.rank.ordinal >= 3)                   unlocked += allTitles.first { it.id == "b_rank" }
        if (profile.rank.ordinal >= 4)                   unlocked += allTitles.first { it.id == "a_rank" }
        if (profile.rank.ordinal >= 5)                   unlocked += allTitles.first { it.id == "s_rank" }
        return unlocked
    }
}