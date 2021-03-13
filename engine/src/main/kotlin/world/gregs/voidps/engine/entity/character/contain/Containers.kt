package world.gregs.voidps.engine.entity.character.contain

import world.gregs.voidps.cache.config.data.ContainerDefinition
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.definition.ContainerDefinitions
import world.gregs.voidps.network.encode.message
import world.gregs.voidps.network.encode.sendContainerItems
import world.gregs.voidps.network.encode.sendInterfaceItemUpdate
import world.gregs.voidps.utility.get

fun Player.sendContainer(name: String, secondary: Boolean = false) {
    val definitions: ContainerDefinitions = get()
    val containerId = definitions.getId(name)
    val container = container(name, definitions.get(name), secondary)
    sendContainerItems(containerId, container.getItems(), container.getAmounts(), secondary)
}

fun Player.hasContainer(name: String): Boolean {
    return containers.containsKey(name)
}

fun Player.container(definition: ContainerDefinition, secondary: Boolean = false): Container {
    val definitions: ContainerDefinitions = get()
    val name = definitions.getName(definition.id)
    return container(name, definition, secondary)
}

fun Player.container(name: String, secondary: Boolean = false): Container {
    val definitions: ContainerDefinitions = get()
    val container = definitions.get(name)
    return container(name, container, secondary)
}

fun Player.container(name: String, detail: ContainerDefinition, secondary: Boolean = false): Container {
    return containers.getOrPut(if (secondary) "_$name" else name) {
        Container(
            id = detail.id,
            capacity = get<ContainerDefinitions>().get(detail.id).length,
            listeners = mutableListOf(),
            stackMode = detail["stack", StackMode.Normal]
        )
    }.apply {
        if (listeners.isEmpty()) {
            definitions = get()
            listeners.add { updates ->
                sendInterfaceItemUpdate(detail.id, updates.map { Triple(it.index, it.item, it.amount) }, secondary)
            }
        }
    }
}

val Player.inventory: Container
    get() = container("inventory")

val Player.equipment: Container
    get() = container("worn_equipment")

val Player.beastOfBurden: Container
    get() = container("beast_of_burden")

fun Player.purchase(amount: Int): Boolean {
    if (inventory.remove(995, amount)) {
        return true
    }
    message("You don't have enough coins.")
    return false
}