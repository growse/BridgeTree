package bridgeTree

class LowestCardStrategy : PlayerStrategy() {
    override val decisionTree: Tree
        get() = Tree.Node(
                amILeading, Tree.Leaf(playLowestCardInHand),
                Tree.Node(amIfirstToPlayThisTrick, Tree.Leaf(playLowestCardInHand),
                        Tree.Node(canIFollowSuit, Tree.Leaf(playLowestCardFromFollwingSuit), Tree.Leaf(playLowestCardInHand)))
        )

}

data class StrategyBundle(val hand: Hand, val trick: Trick, val tricksSoFar: List<Trick>?, val trumpSuit: Suit?, val dummyHand: Hand)

abstract class PlayerStrategy {
    // Decisions!
    protected val isThisTheFirstTrick = fun(b: StrategyBundle): Boolean {
        return (b.tricksSoFar == null || b.tricksSoFar.isEmpty())
    }

    protected val amILeading = fun(b: StrategyBundle): Boolean {
        return isThisTheFirstTrick(b) && amIfirstToPlayThisTrick(b)
    }

    protected val amIfirstToPlayThisTrick = fun(b: StrategyBundle): Boolean {
        return b.trick.getPlayedCards().all { card -> card == null }
    }

    protected val canIFollowSuit = fun(b: StrategyBundle): Boolean {
        return !b.trick.getPlayedCards().isEmpty() || b.hand.any { card -> card.suit == b.trick.getPlayedCards().first()!!.card.suit }
    }


    // Plays!
    protected val playLowestCardInHand = fun(bundle: StrategyBundle): Card { return bundle.hand.first() }

    protected val playLowestCardFromFollwingSuit = fun(b: StrategyBundle): Card { return b.hand.first { card -> card.suit == b.trick.getPlayedCards().first()!!.card.suit } }


    protected abstract val decisionTree: Tree

    fun suggestCard(hand: Hand, trick: Trick, tricksSoFar: List<Trick>?, trumpSuit: Suit?, dummyHand: Hand): Card {
        return walk(decisionTree, StrategyBundle(hand, trick, tricksSoFar, trumpSuit, dummyHand))
    }

    companion object {
        fun walk(tree: Tree, bundle: StrategyBundle): Card = when (tree) {
            is Tree.Leaf -> tree.toPlay(bundle)
            is Tree.Node -> {
                if (tree.decision(bundle)) {
                    walk(tree.ifTrue, bundle)
                } else {
                    walk(tree.ifFalse, bundle)
                }
            }
        }

    }
}

sealed class Tree {
    class Node(val decision: (StrategyBundle) -> Boolean, val ifTrue: Tree, val ifFalse: Tree) : Tree()
    class Leaf(val toPlay: (StrategyBundle) -> Card) : Tree()
}