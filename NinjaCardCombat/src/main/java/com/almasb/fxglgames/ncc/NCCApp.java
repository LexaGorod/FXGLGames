package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.UI;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class NCCApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Ninja Card Combat");
        settings.setVersion("0.1");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Next Turn") {
            @Override
            protected void onActionBegin() {
                nextTurn();
            }
        }, KeyCode.ENTER);
    }

    private List<Entity> playerCards = new ArrayList<>();
    private List<Entity> enemyCards = new ArrayList<>();

    @Override
    protected void initGame() {
        playerCards.clear();
        enemyCards.clear();

        for (int i = 0; i < 5; i++) {
            playerCards.add(getRandomCard(100 + i*150, 500));
            enemyCards.add(getRandomCard(100 + i*150, 50));
        }

        playerCards.forEach(getGameWorld()::addEntity);
        enemyCards.forEach(getGameWorld()::addEntity);
    }

    private Entity getRandomCard(double x, double y) {
        GameEntity entity = new GameEntity();
        entity.setPosition(new Point2D(x, y));

        Card card = new Card();

        entity.addComponent(new CardComponent(card));

        CardFrame cardView = new CardFrame(card);
        cardView.addCardView(getView(entity));

        entity.setView(cardView);

        return entity;
    }

    private EntityView getView(Entity e) {
        Text text = getUIFactory().newText("", Color.BLACK, 18.0);
        text.textProperty().bind(e.getComponentUnsafe(CardComponent.class).getValue().toStringProperty());

        EntityView view = new EntityView();
        view.addNode(text);

        return view;
    }

    private void nextTurn() {
        for (int i = 0; i < 5; i++) {
            Entity cardEntity = playerCards.get(i);
            Card card = cardEntity.getComponentUnsafe(CardComponent.class).getValue();

            if (card.isAlive()) {
                Entity cardEntity2 = enemyCards.get(i);
                Card card2 = cardEntity2.getComponentUnsafe(CardComponent.class).getValue();

                if (card2.isAlive()) {
                    attack(cardEntity, cardEntity2);
                } else {
                    enemyCards.stream().filter(e -> e.getComponentUnsafe(CardComponent.class).getValue().isAlive()).findAny().ifPresent(c -> {
                        attack(cardEntity, c);
                    });
                }
            }
        }

        // TODO: remove duplicate

        for (int i = 0; i < 5; i++) {
            Entity cardEntity = enemyCards.get(i);
            Card card = cardEntity.getComponentUnsafe(CardComponent.class).getValue();

            if (card.isAlive()) {
                Entity cardEntity2 = playerCards.get(i);
                Card card2 = cardEntity2.getComponentUnsafe(CardComponent.class).getValue();

                if (card2.isAlive()) {
                    attack(cardEntity, cardEntity2);
                } else {
                    playerCards.stream().filter(e -> e.getComponentUnsafe(CardComponent.class).getValue().isAlive()).findAny().ifPresent(c -> {
                        attack(cardEntity, c);
                    });
                }
            }
        }

        if (!isAlive(playerCards)) {
            gameOver("You lose");
        } else if (!isAlive(enemyCards)) {
            gameOver("You win");
        }
    }

    private void attack(Entity e1, Entity e2) {
        int atk = e1.getComponentUnsafe(CardComponent.class).getValue().getAtk();
        int def = e2.getComponentUnsafe(CardComponent.class).getValue().getDef();

        int hp = e2.getComponentUnsafe(CardComponent.class).getValue().getHp() - (atk - def);
        e2.getComponentUnsafe(CardComponent.class).getValue().setHp(hp);
    }

    private boolean isAlive(List<Entity> cards) {
        return cards.stream()
                .map(e -> e.getComponentUnsafe(CardComponent.class).getValue())
                .anyMatch(Card::isAlive);
    }

    private void gameOver(String message) {
        getDisplay().showMessageBox(message, this::startNewGame);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
