package entities;

import static utilz.Constants.PlayerConstants.ATTACK_1;
import static utilz.Constants.PlayerConstants.GetSpriteAmount;
import static utilz.Constants.PlayerConstants.IDLE;
import static utilz.Constants.PlayerConstants.RUNNING;
import static utilz.HelpMethods.CanMoveHere;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import utilz.LoadSave;

public class Player extends Entity {

	private BufferedImage[][] animations;
	private int aniTick, aniIndex, aniSpeed = 15;
	private int player_action = IDLE;
	private boolean left, up, right, down;
	private boolean moving = false, attacking = false;
	private float playerSpeed = 2.0f;
	private int[][] lvlData;
	private float xDrawOffSet = 21 * Game.SCALE;
	private float yDrawOffSet = 4 * Game.SCALE;

	public Player(float x, float y, int width, int height) {
		super(x, y, width, height);
		loadAnimations();
		initHitbox(x, y, 20 * Game.SCALE, 28 * Game.SCALE);
	}

	public void update() {
		updatePos();
		updateAnimationTick();
		setAnimation();
	}

	public void render(Graphics g) {
		g.drawImage(animations[player_action][aniIndex], (int) (hitbox.x - xDrawOffSet), (int) (hitbox.y - yDrawOffSet),
				width, height, null);
		drawHitBox(g);
	}

	private void updateAnimationTick() {

		aniTick++;
		if (aniTick >= aniSpeed) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(player_action)) {
				aniIndex = 0;
				attacking = false;
			}
		}

	}

	private void setAnimation() {
		int startAni = player_action;

		if (moving) {
			player_action = RUNNING;
		} else {
			player_action = IDLE;
		}

		if (attacking) {
			player_action = ATTACK_1;
		}

		if (startAni != player_action) {
			resetAniTick();
		}
	}

	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;

	}

	private void updatePos() {

		moving = false;
		if (!left && !right && !up && !down) {
			return;
		}

		float xSpeed = 0, ySpeed = 0;

		if (left && !right) {
			xSpeed -= playerSpeed;
		} else if (right && !left) {
			xSpeed += playerSpeed;
		}

		if (up && !down) {
			ySpeed -= playerSpeed;
		} else if (down && !up) {
			ySpeed += playerSpeed;
		}

//		if (CanMoveHere(x + xSpeed, y + ySpeed, width, height, lvlData)) {
//			this.x += xSpeed;
//			this.y += ySpeed;
//			moving = true;
//		}

		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y + ySpeed, hitbox.width, hitbox.height, lvlData)) {
			hitbox.x += xSpeed;
			hitbox.y += ySpeed;
			moving = true;
		}

	}

	private void loadAnimations() {

		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

		animations = new BufferedImage[9][6];

		for (int j = 0; j < animations.length; j++)
			for (int i = 0; i < animations[j].length; i++)
				animations[j][i] = img.getSubimage(i * 64, j * 40, 64, 40);
	}

	public void loadLvlData(int[][] lvlData) {
		this.lvlData = lvlData;
	}

	public void resetDirBooleans() {
		left = false;
		right = false;
		up = false;
		down = false;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

}
