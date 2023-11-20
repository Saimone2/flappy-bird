package com.saimone.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	private static final int NUMBER_OF_PIPES = 15; // number of generated pipe columns
	private static final int GAP = 800; // vertical pipe spacing (700 - 1100 recommended)
	private static final int DISTANCE_BETWEEN_PIPES = 10; // distance between pipe columns (7 - 17 recommended)
	private static final int INITIAL_BIRD_SPEED = 7; // initial velocity of the bird (3 - 12 recommended)
	private static final int BIRD_ACCELERATION = 1; // bird acceleration (0 - 10 recommended)
	private static final int BIRD_FLIPPING_POWER = 22; // bird flipping power (18 - 26 recommended)
	private static final float GRAVITY = 9; // bird flipping power (8 - 16 recommended)


	SpriteBatch batch;
	Texture backgroundDay, backgroundNight;
	Texture topPipe, bottomPipe;
	Texture[] birds;
	Texture gameOver;
	Circle birdCircle;
	Rectangle[] topPipeRectangle;
	Rectangle[] bottomPipeRectangle;
	BitmapFont font;
	Random randomGenerator;
	FreeTypeFontGenerator generator;
	boolean isDay = true;
	float stateTime, dayTimer = 0, delayTimer = 0;
	int flapState = 0, gameState = 0, score = 0, scoringPipe = 0;
	float birdWidth, birdHeight, birdY, birdRotation, birdVelocity, acceleration;
	float gameOverWidth, gameOverHeight;
	float velocity = 0, gravity;
	float pipeWidth, pipeHeight, maxPipeOffset, distanceBetweenPipes, increasingDistance;
	float[] pipeX = new float[NUMBER_OF_PIPES];
	float[] pipeOffset = new float[NUMBER_OF_PIPES];

	@Override
	public void create() {
		batch = new SpriteBatch();
		Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		backgroundDay = new Texture("background-day.png");
		backgroundNight = new Texture("background-night.png");
		birdCircle = new Circle();
		topPipeRectangle = new Rectangle[NUMBER_OF_PIPES];
		bottomPipeRectangle = new Rectangle[NUMBER_OF_PIPES];
		gameOver = new Texture("gameover.png");
		generator = new FreeTypeFontGenerator(Gdx.files.internal("flappy-font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 190;
		parameter.color = Color.WHITE;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 4;
		font = generator.generateFont(parameter);

		birds = new Texture[3];
		birds[0] = new Texture("yellowbird-upflap.png");
		birds[1] = new Texture("yellowbird-midflap.png");
		birds[2] = new Texture("yellowbird-downflap.png");

		topPipe = new Texture("pipe-green-top.png");
		bottomPipe = new Texture("pipe-green-bottom.png");
		maxPipeOffset = Gdx.graphics.getHeight() / 2f - GAP / 2f - 100;

		randomGenerator = new Random();
		gravity = GRAVITY * 0.1f;
		birdVelocity = INITIAL_BIRD_SPEED;
		acceleration = BIRD_ACCELERATION * 0.001f;
		increasingDistance = BIRD_ACCELERATION * 0.05f;
		distanceBetweenPipes = Gdx.graphics.getWidth() / ((1f / DISTANCE_BETWEEN_PIPES) * 14);
		startGame();
	}

	public void startGame() {
		birdY = Gdx.graphics.getHeight() / 2f - birds[0].getHeight() * 5 / 2f;
		birdRotation = 0;

		for (int i = 0; i < NUMBER_OF_PIPES; i++) {
			pipeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - GAP - 200);
			pipeX[i] = Gdx.graphics.getWidth() + i * distanceBetweenPipes;

			topPipeRectangle[i] = new Rectangle();
			bottomPipeRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render() {
		batch.begin();

		pipeWidth = topPipe.getWidth() * 5;
		pipeHeight = topPipe.getHeight() * 5;
		batch.draw(backgroundDay, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {
			stateTime += Gdx.graphics.getDeltaTime();
			if (stateTime > 0.1f) {
				if (flapState == 0) {
					flapState = 1;
				} else if (flapState == 1) {
					flapState = 2;
				} else {
					flapState = 0;
				}
				stateTime = 0;
			}

			dayTimer += Gdx.graphics.getDeltaTime();
			if (dayTimer >= 30) {
				dayTimer = 0;
				isDay = !isDay;
			}
			if (isDay) {
				batch.draw(backgroundDay, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			} else {
				batch.draw(backgroundNight, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			}

			if (pipeX[scoringPipe] < Gdx.graphics.getWidth() / 2f) {
				score++;
				if (scoringPipe < NUMBER_OF_PIPES - 1) {
					scoringPipe++;
				} else {
					scoringPipe = 0;
				}
			}

			birdVelocity = birdVelocity + acceleration;
			distanceBetweenPipes = distanceBetweenPipes + increasingDistance;
			if (Gdx.input.justTouched()) {
				velocity = -BIRD_FLIPPING_POWER;
				birdRotation = 30;
			}

			for (int i = 0; i < NUMBER_OF_PIPES; i++) {
				if (pipeX[i] < -pipeWidth) {
					pipeX[i] += NUMBER_OF_PIPES * distanceBetweenPipes;
				} else {
					pipeX[i] = pipeX[i] - birdVelocity;
				}

				batch.draw(topPipe, pipeX[i], Gdx.graphics.getHeight() / 2f + GAP / 3f + pipeOffset[i], pipeWidth, pipeHeight);
				batch.draw(bottomPipe, pipeX[i], -GAP + pipeOffset[i], pipeWidth, pipeHeight);

				topPipeRectangle[i] = new Rectangle(pipeX[i] + 15, Gdx.graphics.getHeight() / 2f + GAP / 3f + pipeOffset[i] + 5, pipeWidth - 30, pipeHeight - 5);
				bottomPipeRectangle[i] = new Rectangle(pipeX[i] + 15, -GAP + pipeOffset[i], pipeWidth - 30, pipeHeight - 5);
			}

			if (birdY >= 30 && birdY + birdHeight + 10 <= Gdx.graphics.getHeight()) {
				velocity = velocity + gravity;
				birdY -= velocity;
			} else {
				gameState = 2;
			}

			if (birdRotation > -60) {
				birdRotation = birdRotation - 1.2f;
			}
		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2) {
			if (isDay) {
				batch.draw(backgroundDay, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			} else {
				batch.draw(backgroundNight, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			}

			for (int i = 0; i < NUMBER_OF_PIPES; i++) {
				batch.draw(topPipe, pipeX[i], Gdx.graphics.getHeight() / 2f + GAP / 3f + pipeOffset[i], pipeWidth, pipeHeight);
				batch.draw(bottomPipe, pipeX[i], -GAP + pipeOffset[i], pipeWidth, pipeHeight);
			}

			gameOverWidth = gameOver.getWidth() * 4;
			gameOverHeight = gameOver.getHeight() * 4;

			font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2f - 41, Gdx.graphics.getHeight() / 1.2f);

			delayTimer += Gdx.graphics.getDeltaTime();
			if (Gdx.input.justTouched() && delayTimer >= 1.5) {
				gameState = 1;
				score = 0;
				scoringPipe = 0;
				velocity = 0;
				stateTime = 0;
				dayTimer = 0;
				delayTimer = 0;
				isDay = true;
				startGame();
			}

			birdWidth = birds[flapState].getWidth() * 5;
			birdHeight = birds[flapState].getHeight() * 5;
			batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2f - birdWidth / 2, birdY,
					birdWidth / 2, birdHeight / 2, birdWidth, birdHeight, 1, 1, birdRotation, 0, 0,
					birds[flapState].getWidth(), birds[flapState].getHeight(), false, false);

			batch.draw(gameOver, Gdx.graphics.getWidth() / 2f - gameOverWidth / 2f, Gdx.graphics.getHeight() / 1.5f - gameOverHeight / 2f, gameOverWidth, gameOverHeight);

		}
		font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2f - 41, Gdx.graphics.getHeight() / 1.2f);

		if (gameState != 2) {
			birdWidth = birds[flapState].getWidth() * 5;
			birdHeight = birds[flapState].getHeight() * 5;

			batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2f - birdWidth / 2, birdY,
					birdWidth / 2, birdHeight / 2, birdWidth, birdHeight, 1, 1, birdRotation, 0, 0,
					birds[flapState].getWidth(), birds[flapState].getHeight(), false, false);

			birdCircle.set(Gdx.graphics.getWidth() / 2f, birdY + birdHeight / 2f, birdWidth / 2f - 10);
			for (int i = 0; i < NUMBER_OF_PIPES; i++) {
				if (Intersector.overlaps(birdCircle, topPipeRectangle[i]) || Intersector.overlaps(birdCircle, bottomPipeRectangle[i])) {
					gameState = 2;
					break;
				}
			}
		}
		batch.end();
	}

	@Override
	public void dispose() {
		generator.dispose();
		backgroundDay.dispose();
		backgroundNight.dispose();
		topPipe.dispose();
		bottomPipe.dispose();
		gameOver.dispose();
		for (Texture bird : birds) {
			bird.dispose();
		}
		batch.dispose();
	}
}