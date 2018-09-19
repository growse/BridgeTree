package bridgeTree

enum class Pip {
    TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE;

    fun short(): String {
        return if (this.ordinal <= 8) {
            (this.ordinal + 2).toString()
        } else {
            this.name.substring(0, 1)
        }
    }
}