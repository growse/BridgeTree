package BridgeTree

data class Card(val suit: Suit, val pip: Pip)

fun Card.toCardString(): String {
    return "${this.pip} of ${this.suit}"
}