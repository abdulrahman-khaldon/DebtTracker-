package com.debttracker.app.ui.navigation

/** Navigation routes. */
sealed class Screen(val route: String) {

    data object Home : Screen("home")

    data object AddEditContact : Screen("add_edit_contact?contactId={contactId}") {
        const val ARG_CONTACT_ID = "contactId"

        fun createRoute(contactId: Long = NEW_CONTACT) = "add_edit_contact?contactId=$contactId"

        const val NEW_CONTACT: Long = -1L
    }

    data object ContactDetail : Screen("contact_detail/{contactId}") {
        const val ARG_CONTACT_ID = "contactId"

        fun createRoute(contactId: Long) = "contact_detail/$contactId"
    }

    data object Settings : Screen("settings")
}
