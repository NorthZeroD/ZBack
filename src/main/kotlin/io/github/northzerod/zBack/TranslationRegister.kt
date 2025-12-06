package io.github.northzerod.zBack

import net.kyori.adventure.key.Key
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationStore
import net.kyori.adventure.util.UTF8ResourceBundleControl
import java.util.*


class TranslationRegister {
    init {
        val store = TranslationStore.messageFormat(Key.key("namespace:value"))
        val locales = listOf(Locale.US, Locale.CHINA)
        for (locale in locales) {
            val bundle = ResourceBundle.getBundle(
                "locales.Bundle", locale,
                UTF8ResourceBundleControl.utf8ResourceBundleControl()
            )
            store.registerAll(locale, bundle, true)
        }
        GlobalTranslator.translator().addSource(store)
    }
}