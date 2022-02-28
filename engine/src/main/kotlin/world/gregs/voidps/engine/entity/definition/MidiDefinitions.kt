package world.gregs.voidps.engine.entity.definition

import world.gregs.voidps.engine.data.FileStorage
import world.gregs.voidps.engine.entity.definition.config.MidiDefinition
import world.gregs.voidps.engine.timedLoad
import world.gregs.voidps.engine.utility.get
import world.gregs.voidps.engine.utility.getProperty

class MidiDefinitions : DefinitionsDecoder<MidiDefinition> {

    override lateinit var definitions: Array<MidiDefinition>
    override lateinit var ids: Map<String, Int>

    fun load(storage: FileStorage = get(), path: String = getProperty("midiDefinitionsPath")): MidiDefinitions {
        timedLoad("midi definition") {
            val data = storage.loadMapIds(path)
            definitions = Array(data.maxOf { it.value["id"] as Int }) { MidiDefinition(id = it, stringId = it.toString()) }
            decode(data)
        }
        return this
    }

    override fun empty() = MidiDefinition.EMPTY

}