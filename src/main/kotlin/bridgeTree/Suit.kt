package bridgeTree

enum class Suit {
    CLUBS, DIAMONDS, HEARTS, SPADES;

    fun short(): String {
        return this.name.substring(0, 1)
    }
}