package world.gregs.voidps.engine.entity.obj

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.koin.test.mock.declareMock
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.engine.client.cacheDefinitionModule
import world.gregs.voidps.engine.entity.definition.ObjectDefinitions
import world.gregs.voidps.engine.event.EventHandlerStore
import world.gregs.voidps.engine.map.Tile
import world.gregs.voidps.engine.map.collision.Collisions
import world.gregs.voidps.engine.path.strat.DecorationTargetStrategy
import world.gregs.voidps.engine.path.strat.EntityTileTargetStrategy
import world.gregs.voidps.engine.path.strat.RectangleTargetStrategy
import world.gregs.voidps.engine.path.strat.WallTargetStrategy
import world.gregs.voidps.engine.script.KoinMock
import world.gregs.voidps.engine.utility.get

internal class GameObjectFactoryTest : KoinMock() {

    lateinit var factory: GameObjectFactory

    lateinit var collisions: Collisions

    override val modules = listOf(cacheDefinitionModule)

    @BeforeEach
    fun setup() {
        collisions = mockk(relaxed = true)
        val store: EventHandlerStore = mockk(relaxed = true)
        declareMock<ObjectDefinitions> {
            every { this@declareMock.get(any<String>()) } returns ObjectDefinition(id = 1, blockFlag = 1)
        }
        factory = GameObjectFactory(collisions, store, get())
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 9])
    fun `Wall object strategy`(type: Int) {
        // When
        val gameObject = factory.spawn("1", Tile.EMPTY, type, 0)
        // Then
        assert(gameObject.interactTarget is WallTargetStrategy)
    }

    @ParameterizedTest
    @ValueSource(ints = [3, 4, 5, 6, 7, 8])
    fun `Wall decoration object strategy`(type: Int) {
        // When
        val gameObject = factory.spawn("1", Tile.EMPTY, type, 0)
        // Then
        assert(gameObject.interactTarget is DecorationTargetStrategy)
    }

    @ParameterizedTest
    @ValueSource(ints = [10, 11, 22])
    fun `Floor decoration object strategy`(type: Int) {
        // When
        val gameObject = factory.spawn("1", Tile.EMPTY, type, 0)
        // Then
        assert(gameObject.interactTarget is RectangleTargetStrategy)
    }

    @ParameterizedTest
    @ValueSource(ints = [12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 23, 24])
    fun `Other object strategy`(type: Int) {
        // When
        val gameObject = factory.spawn("1", Tile.EMPTY, type, 0)
        // Then
        assertEquals(
            EntityTileTargetStrategy(gameObject),
            gameObject.interactTarget
        )
    }

}