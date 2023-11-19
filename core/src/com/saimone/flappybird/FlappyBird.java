package com.saimone.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture topTube, bottomTube;
	Texture[] birds;
	float stateTime;
	int flapState = 0, gameState = 0;
	float birdWidth, birdHeight, birdY;
	float velocity = 0, gravity = 1;
	float gap = 800, tubeVelocity = 4;
	float tubeWidth, tubeHeight, maxTubeOffset, distanceBetweenTubes;
	int numberOfTubers = 4;
	float[] tubeX = new float[numberOfTubers];
	float[] tubeOffset = new float[numberOfTubers];
	Random randomGenerator;

	@Override
	public void create() {
		batch = new SpriteBatch();
		background = new Texture("background-day.png");
		birds = new Texture[3];
		birds[0] = new Texture("yellowbird-upflap.png");
		birds[1] = new Texture("yellowbird-midflap.png");
		birds[2] = new Texture("yellowbird-downflap.png");

		birdY = Gdx.graphics.getHeight() / 2f - birds[0].getHeight() * 5 / 2f;

		topTube = new Texture("pipe-green-top.png");
		bottomTube = new Texture("pipe-green.png");

		maxTubeOffset = Gdx.graphics.getHeight() / 2f - gap / 2 - 100;

		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() / 1.5f;

		for (int i = 0; i < numberOfTubers; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth() / 2f - tubeWidth / 2 + i * distanceBetweenTubes;
		}
	}

	@Override
	public void render() {
		ScreenUtils.clear(1, 0, 0, 1);

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		tubeWidth = topTube.getWidth() * 5;
		tubeHeight = topTube.getHeight() * 5;

		if (gameState != 0) {
			if (Gdx.input.justTouched()) {
				velocity = -22;
			}

			for (int i = 0; i < numberOfTubers; i++) {
				tubeX[i] = tubeX[i] - tubeVelocity;

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2f + gap / 3 + tubeOffset[i], tubeWidth, tubeHeight);
				batch.draw(bottomTube, tubeX[i], 0 - gap + tubeOffset[i], tubeWidth, tubeHeight);
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
	}

	@Override
	public void dispose() {
		batch.dispose();
		background.dispose();
		for (Texture bird : birds) {
			bird.dispose();
		}
	}
}