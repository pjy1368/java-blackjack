package blackjack.domain.participants;

import blackjack.domain.card.CardDeck;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Participants {

    private final List<Participant> participantGroup;

    public Participants(final Names names) {
        participantGroup = participantsSetUp(names);
    }

    private List<Participant> participantsSetUp(final Names names) {
        final List<Participant> participants = names.toList().stream()
            .map(Player::new)
            .collect(Collectors.toList());
        participants.add(0, new Dealer());
        return new ArrayList<>(participants);
    }

    public void distributeCard() {
        participantGroup.forEach(participant -> {
            participant.receiveCard(CardDeck.distribute());
            participant.receiveCard(CardDeck.distribute());
        });
    }

    public Participant getDealer() {
        return participantGroup.get(0);
    }

    public List<Participant> getPlayers() {
        return Collections.unmodifiableList(participantGroup.subList(1, participantGroup.size()));
    }

    public List<Participant> getParticipantGroup() {
        return Collections.unmodifiableList(participantGroup);
    }
}
