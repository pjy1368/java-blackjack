package blackjack.controller;

import blackjack.domain.CardDeck;
import blackjack.domain.Dealer;
import blackjack.domain.Player;
import blackjack.view.InputView;
import blackjack.view.OutputView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlackjackController {
    private static final String END_GAME_MARK = "n";

    public void run() {
        final CardDeck cardDeck = new CardDeck();
        final Dealer dealer = new Dealer("딜러");
        final List<Player> players = playerSetUp();

        distributeCard(players, dealer, cardDeck);
        showDistributeStatus(players);
        showDistributedCard(players, dealer);
        playerGameProgress(players, cardDeck);
        dealerGameProgress(dealer, cardDeck);
        showFinalCardResult(players, dealer);
        showGameResult(players, dealer);
    }

    private List<Player> playerSetUp() {
        final List<String> names = InputView.requestName();
        final List<Player> players = new ArrayList<>();
        for (String name : names) {
            players.add(new Player(name));
        }
        return players;
    }

    private void distributeCard(final List<Player> players, final Dealer dealer, final CardDeck cardDeck) {
        for (Player player : players) {
            player.receiveCard(cardDeck.distribute());
            player.receiveCard(cardDeck.distribute());
        }
        dealer.receiveCard(cardDeck.distribute());
        dealer.receiveCard(cardDeck.distribute());
    }

    private void showDistributeStatus(final List<Player> players) {
        String status = players.stream()
                .map(Player::getName)
                .collect(Collectors.joining(", "));
        OutputView.distributeMessage(status);
    }

    private void showDistributedCard(final List<Player> players, final Dealer dealer) {
        OutputView.showDealerCard(dealer.getName(), dealer.getMyCards().get(0));
        for (final Player player : players) {
            OutputView.showPlayerCard(player.getName(), player.getMyCards());
        }
    }

    private void playerGameProgress(final List<Player> players, final CardDeck cardDeck) {
        for (Player player : players) {
            singlePlayerGameProgress(cardDeck, player);
        }
    }

    private void singlePlayerGameProgress(CardDeck cardDeck, Player player) {
        if (END_GAME_MARK.equals(InputView.askMoreCard(player.getName()))) {
            OutputView.showPlayerCard(player.getName(), player.getMyCards());
            return;
        }
        player.receiveCard(cardDeck.distribute());
        OutputView.showPlayerCard(player.getName(), player.getMyCards());
        if (isBust(player)) {
            return;
        }
        singlePlayerGameProgress(cardDeck, player);
    }

    private boolean isBust(Player player) {
        if (player.isBust()) {
            OutputView.bustMessage();
            return true;
        }
        return false;
    }

    private void dealerGameProgress(final Dealer dealer, final CardDeck cardDeck) {
        while (dealer.checkMoreCardAvailable()) {
            OutputView.dealerMoreCard();
            dealer.receiveCard(cardDeck.distribute());
        }
    }

    private void showFinalCardResult(final List<Player> players, final Dealer dealer) {
        OutputView.showCardResult(dealer.getName(), dealer.getMyCards(), dealer.calculate());
        for (Player player : players) {
            OutputView.showCardResult(player.getName(), player.getMyCards(), player.calculate());
        }
    }

    private void showGameResult(final List<Player> players, final Dealer dealer) {
        int winCount = 0;
        for (final Player player : players) {
            if (dealer.isWinner(player.calculate()) || player.isBust()) {
                winCount++;
                player.lose();
            }
        }
        OutputView.showGameResult(dealer.getName(), winCount, players.size() - winCount);
        for (final Player player : players) {
            OutputView.showPlayerGameResult(player.getName(), player.getWin());
        }
    }
}
