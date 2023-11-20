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
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture backgroundDay, backgroundNight;
	Texture topPipe, bottomPipe;
	Texture[] birds;
	Circle birdCircle;
	Rectangle[] topPipeRectangle;
	Rectangle[] bottomPipeRectangle;
	BitmapFont font;
	Random randomGenerator;
	FreeTypeFontGenerator generator;
	boolean isDay = true;
	float stateTime, dayTimer = 0;
	int flapState = 0, gameState = 0, score = 0, scoringPipe = 0;
	float birdWidth, birdHeight, birdY, birdRotation;
	float velocity = 0, gravity = 1;
	float gap = 800, tubeVelocity = 7;
	float pipeWidth, pipeHeight, maxPipeOffset, distanceBetweenPipes;
	int numberOfPipes = 15;
	float[] pipeX = new float[numberOfPipes];
	float[] pipeOffset = new float[numberOfPipes];

	@Override
	public void create() {
		batch = new SpriteBatch();
		backgroundDay = new Texture("background-day.png");
		backgroundNight = new Texture("background-night.png");
		birdCircle = new Circle();
		topPipeRectangle = new Rectangle[numberOfPipes];
		bottomPipeRectangle = new Rectangle[numberOfPipes];

		generator = new FreeTypeFontGenerator(Gdx.files.internal("flappy-font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 190;
		parameter.color = Color.WHITE;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 1;
		font = generator.generateFont(parameter);

		birds = new Texture[3];
		birds[0] = new Texture("yellowbird-upflap.png");
		birds[1] = new Texture("yellowbird-midflap.png");
		birds[2] = new Texture("yellowbird-downflap.png");

		birdY = Gdx.graphics.getHeight() / 2f - birds[0].getHeight() * 5 / 2f;
		birdRotation = 0;

		topPipe = new Texture("pipe-green-top.png");
		bottomPipe = new Texture("pipe-green.png");

		maxPipeOffset = Gdx.graphics.getHeight() / 2f - gap / 2 - 100;

		randomGenerator = new Random();
		distanceBetweenPipes = Gdx.graphics.getWidth() / 1.4f;

		for (int i = 0; i < numberOfPipes; i++) {
			pipeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			pipeX[i] = Gdx.graphics.getWidth() + i * distanceBetweenPipes;

			topPipeRectangle[i] = new Rectangle();
			bottomPipeRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render() {
		ScreenUtils.clear(1, 0, 0, 1);

		batch.begin();

		pipeWidth = topPipe.getWidth() * 5;
		pipeHeight = topPipe.getHeight() * 5;

		batch.draw(backgroundDay, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState != 0) {
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

			if(pipeX[scoringPipe] < Gdx.graphics.getWidth() / 2f) {
				score++;
				if(scoringPipe < numberOfPipes - 1) {
					scoringPipe++;
				} else {
					scoringPipe = 0;
				}
			}

			tubeVelocity = tubeVelocity + 0.001f;
			distanceBetweenPipes = distanceBetweenPipes + 0.04f;
			if (Gdx.input.justTouched()) {
				velocity = -22;
				birdRotation = 30;
			}

			for (int i = 0; i < numberOfPipes; i++) {
				if (pipeX[i] < -pipeWidth) {
					pipeX[i] += numberOfPipes * distanceBetweenPipes;
				} else {
					pipeX[i] = pipeX[i] - tubeVelocity;
				}

				batch.draw(topPipe, pipeX[i], Gdx.graphics.getHeight() / 2f + gap / 3 + pipeOffset[i], pipeWidth, pipeHeight);
				batch.draw(bottomPipe, pipeX[i], 0 - gap + pipeOffset[i], pipeWidth, pipeHeight);

				topPipeRectangle[i] = new Rectangle(pipeX[i] + 15, Gdx.graphics.getHeight() / 2f + gap / 3 + pipeOffset[i] + 5, pipeWidth - 30, pipeHeight - 5);
				bottomPipeRectangle[i] = new Rectangle(pipeX[i] + 15, 0 - gap + pipeOffset[i], pipeWidth - 30, pipeHeight - 5);
			}

			if (birdY > 0 || velocity < 0) {
				velocity = velocity + gravity;
				birdY -= velocity;
			}
			if (birdRotation > -60) {
				birdRotation = birdRotation - 1.2f;
			}
		} else {
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		}

		float flapAnimationDelay = 0.1f;
		stateTime += Gdx.graphics.getDeltaTime();
		if (stateTime > flapAnimationDelay) {
			if (flapState == 0) {
				flapState = 1;
			} else if (flapState == 1) {
				flapState = 2;
			} else {
				flapState = 0;
			}
			stateTime = 0;
		}

		font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2f - 41, Gdx.graphics.getHeight() / 1.2f);

		birdWidth = birds[flapState].getWidth() * 5;
		birdHeight = birds[flapState].getHeight() * 5;

		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2f - birdWidth / 2, birdY,
				birdWidth / 2, birdHeight / 2, birdWidth, birdHeight, 1, 1, birdRotation, 0, 0,
				birds[flapState].getWidth(), birds[flapState].getHeight(), false, false);
		batch.end();

		birdCircle.set(Gdx.graphics.getWidth() / 2f, birdY + birdHeight / 2f, birdWidth / 2f - 10);
		for (int i = 0; i < numberOfPipes; i++) {
			if (Intersector.overlaps(birdCircle, topPipeRectangle[i]) || Intersector.overlaps(birdCircle, bottomPipeRectangle[i])) {
				gameState = 0;
				break;
			}
		}
	}

	@Override
	public void dispose() {
		generator.dispose();
		batch.dispose();
		backgroundDay.dispose();
		backgroundNight.dispose();
		topPipe.dispose();
		bottomPipe.dispose();
		for (Texture bird : birds) {
			bird.dispose();
		}
	}
}