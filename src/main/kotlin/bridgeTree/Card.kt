package bridgeTree

data class Card(val suit: Suit, val pip: Pip) : Comparable<Card> {
    companion object {
        fun totalNumberOfCards(): Int {
            return Pip.values().size * Suit.values().size
        }
    }

    override fun compareTo(other: Card): Int {
        if (this.equals(other)) return 0
        if (this.suit > other.suit) return 1
        if (this.suit < other.suit) return -1
        if (this.pip > other.pip) return 1
        return -1
    }

    fun toLongString(): String {
        return "${this.pip} of ${this.suit}"
    }

    override fun toString(): String {
        val shortpip = this.pip.short()
        val shortsuit = this.suit.short()
        return "${shortpip}${shortsuit}"
    }
}

