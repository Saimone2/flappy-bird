package com.saimone.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture backgroundDay, backgroundNight;
	Texture topTube, bottomTube;
	Texture[] birds;
	Circle birdCircle;
	Rectangle[] topPipeRectangle;
	Rectangle[] bottomPipeRectangle;
	boolean isDay = true;
	float stateTime, dayTimer = 0;
	int flapState = 0, gameState = 0;
	float birdWidth, birdHeight, birdY;
	float velocity = 0, gravity = 1;
	float gap = 800, tubeVelocity = 7;
	float tubeWidth, tubeHeight, maxTubeOffset, distanceBetweenTubes;
	int numberOfPipes = 15;
	float[] tubeX = new float[numberOfPipes];
	float[] tubeOffset = new float[numberOfPipes];
	Random randomGenerator;

	@Override
	public void create() {
		batch = new SpriteBatch();
		backgroundDay = new Texture("background-day.png");
		backgroundNight = new Texture("background-night.png");
		birdCircle = new Circle();
		topPipeRectangle = new Rectangle[numberOfPipes];
		bottomPipeRectangle = new Rectangle[numberOfPipes];


		birds = new Texture[3];
		birds[0] = new Texture("yellowbird-upflap.png");
		birds[1] = new Texture("yellowbird-midflap.png");
		birds[2] = new Texture("yellowbird-downflap.png");

		birdY = Gdx.graphics.getHeight() / 2f - birds[0].getHeight() * 5 / 2f;

		topTube = new Texture("pipe-green-top.png");
		bottomTube = new Texture("pipe-green.png");

		maxTubeOffset = Gdx.graphics.getHeight() / 2f - gap / 2 - 100;

		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() / 1.4f;

		for (int i = 0; i < numberOfPipes; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			topPipeRectangle[i] = new Rectangle();
			bottomPipeRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render() {
		ScreenUtils.clear(1, 0, 0, 1);

		batch.begin();

		tubeWidth = topTube.getWidth() * 5;
		tubeHeight = topTube.getHeight() * 5;

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

			tubeVelocity= tubeVelocity + 0.001f;
			distanceBetweenTubes = distanceBetweenTubes + 0.04f;
			if (Gdx.input.justTouched()) {
				velocity = -22;
			}

			for (int i = 0; i < numberOfPipes; i++) {
				if (tubeX[i] < -tubeWidth) {
					tubeX[i] += numberOfPipes * distanceBetweenTubes;
				} else {
					tubeX[i] = tubeX[i] - tubeVelocity;
					batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2f + gap / 3 + tubeOffset[i], tubeWidth, tubeHeight);
					batch.draw(bottomTube, tubeX[i], 0 - gap + tubeOffset[i], tubeWidth, tubeHeight);

					topPipeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2f + gap / 3 + tubeOffset[i], tubeWidth, tubeHeight);
					bottomPipeRectangle[i] = new Rectangle(tubeX[i], 0 - gap + tubeOffset[i], tubeWidth, tubeHeight);
				}
			}

			if (birdY > 0 || velocity < 0) {
				velocity = velocity + gravity;
				birdY -= velocity;
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

		birdWidth = birds[flapState].getWidth() * 5;
		birdHeight = birds[flapState].getHeight() * 5;

		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2f - birdWidth / 2, birdY, birdWidth, birdHeight);
		batch.end();

		birdCircle.set(Gdx.graphics.getWidth() / 2f, birdY + birdHeight / 2f, birdWidth / 2f);
		for (int i = 0; i < numberOfPipes; i++) {
			if (Intersector.overlaps(birdCircle, topPipeRectangle[i]) || Intersector.overlaps(birdCircle, bottomPipeRectangle[i])) {
				gameState = 0;
				break;
			}
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
		backgroundDay.dispose();
		backgroundNight.dispose();
		topTube.dispose();
		bottomTube.dispose();
		for (Texture bird : birds) {
			bird.dispose();
		}
	}
}