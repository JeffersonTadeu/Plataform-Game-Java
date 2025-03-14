package entities;

import static utilz.Constants.PlayerConstants.ATTACK_1;
import static utilz.Constants.PlayerConstants.FALLING;
import static utilz.Constants.PlayerConstants.GetSpriteAmount;
import static utilz.Constants.PlayerConstants.IDLE;
import static utilz.Constants.PlayerConstants.JUMP;
import static utilz.Constants.PlayerConstants.RUNNING;
import static utilz.HelpMethods.CanMoveHere;
import static utilz.HelpMethods.GetEntityXPosNextToWall;
import static utilz.HelpMethods.GetEntityYPosUnderRoofOrAboveFloor;
import static utilz.HelpMethods.IsEntityOnFloor;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import utilz.LoadSave;

public class Player extends Entity {

	private BufferedImage[][] animations;
	private int aniTick, aniIndex, aniSpeed = 15;
	private int player_action = IDLE;
	private boolean left, up, right, down, jump;
	private boolean moving = false, attacking = false;
	private float playerSpeed = 1.0f * Game.SCALE;
	private int[][] lvlData;
	private float xDrawOffSet = 21 * Game.SCALE;
	private float yDrawOffSet = 4 * Game.SCALE;

	// Jumping / Gravity
	private float airSpeed = 0f;
	private float gravity = 0.04f * Game.SCALE;
	private float jumpSpeed = -2.25f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
	private boolean inAir = false;

	public Player(float x, float y, int width, int height) {
		super(x, y, width, height);
		loadAnimations();
		initHitbox(x, y, (int) (20 * Game.SCALE), (int) (27 * Game.SCALE));

	}

	public void update() {
		updatePos();
		updateAnimationTick();
		setAnimation();
	}

	public void render(Graphics g) {
		g.drawImage(animations[player_action][aniIndex], (int) (hitbox.x - xDrawOffSet), (int) (hitbox.y - yDrawOffSet),
				width, height, null);
//		drawHitBox(g);
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

		if (inAir) {
			if (airSpeed < 0) {
				player_action = JUMP;
			} else {
				player_action = FALLING;
			}
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

		if (jump) {
			jump();
		}
		if (!left && !right && !inAir) {
			return;
		}

		float xSpeed = 0;

		if (left)
			xSpeed -= playerSpeed;

		if (right)
			xSpeed += playerSpeed;

		if (!inAir) {
			if (!IsEntityOnFloor(hitbox, lvlData)) {
				inAir = true;
			}
		}

		if (inAir) {

			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += gravity;
				updateXPos(xSpeed);
			} else {
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0) {
					resetInAir();
				} else {
					airSpeed = fallSpeedAfterCollision;
				}
				updateXPos(xSpeed);
			}

		} else {
			updateXPos(xSpeed);
		}

		moving = true;
	}

	private void jump() {
		if (inAir) {
			return;
		}
		inAir = true;
		airSpeed = jumpSpeed;

	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;

	}

	private void updateXPos(float xSpeed) {

		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
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
		if (!IsEntityOnFloor(hitbox, lvlData)) {
			inAir = true;
		}
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

	public void setJump(boolean jump) {
		this.jump = jump;
	}
}
