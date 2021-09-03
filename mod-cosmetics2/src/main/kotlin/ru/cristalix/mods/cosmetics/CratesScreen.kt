package ru.cristalix.mods.cosmetics

import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle
import ru.cristalix.uiengine.utility.text

class CratesScreen : ContextGui() {

    val buttonCrate1 = button {
        icon.stack = item("chest")
        title.content = "Чёткий кейс"

        onClick {
            val caseScreen = CaseScreen()
            val context = CaseContextGui(connection, caseScreen)
            caseScreen.setup(WHITE, "ohuet", ItemStack.of(Items.GOLDEN_APPLE, 1, 0))
            context.open()
        }
    }

    val contentWrapper = grid(
        buttonCrate1,
    )

    val body = rectangle {

        size.x = 250.0
        beforeRender {
            size.y = this@CratesScreen.size.y
        }
        color.alpha = 0.82
        addChild(text {
            content = "< [ ESC ] Назад"
            color.alpha = 0.5
            offset = V3(4.0, 4.0)
        })
        addChild(contentWrapper)
        this@CratesScreen.addChild(this)

    }


}
