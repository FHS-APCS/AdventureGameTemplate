package ca.vanzeben.game.level;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import ca.vanzeben.game.entities.Player;
import ca.vanzeben.game.gfx.Screen;
import ca.vanzeben.game.level.tiles.Tile;

public class Level {
	private static final int tileSize = 8;		// from sprite sheet
	private static final int scaleFactor = 2; // how much to scale the tiles up
	
	public static final int levelScaleFactor = tileSize*scaleFactor; // each pixel in game ends up
																								 									 // being this large
	private byte[] tiles;
	private int levelImageWidth;
	private int levelImageHeight;
	private String imagePath;
	private BufferedImage image;

	private Player player;

	public Level(String imagePath) {
		if (imagePath != null) {
			this.imagePath = imagePath;
			this.loadLevelFromFile();
		} else {
			this.levelImageWidth = 64;
			this.levelImageHeight = 64;
			tiles = new byte[levelImageWidth * levelImageHeight];
			this.generateLevel();
		}
	}

	private void loadLevelFromFile() {
		try {
			this.image = ImageIO.read(Level.class.getResource(this.imagePath));
			this.levelImageWidth = this.image.getWidth();
			this.levelImageHeight = this.image.getHeight();
			tiles = new byte[levelImageWidth * levelImageHeight];
			this.loadTiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadTiles() {
		int[] tileColours = this.image.getRGB(0, 0, levelImageWidth,
				levelImageHeight, null, 0, levelImageWidth);
		for (int y = 0; y < levelImageHeight; y++) {
			for (int x = 0; x < levelImageWidth; x++) {
				tileCheck: for (Tile t : Tile.tiles) {
					if (t != null
							&& t.getLevelColour() == tileColours[x + y * levelImageWidth]) {
						this.tiles[x + y * levelImageWidth] = t.getId();
						break tileCheck;
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void saveLevelToFile() {
		try {
			ImageIO.write(image, "png",
					new File(Level.class.getResource(this.imagePath).getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void alterTile(int x, int y, Tile newTile) {
		this.tiles[x + y * levelImageWidth] = newTile.getId();
		image.setRGB(x, y, newTile.getLevelColour());
	}

	public void generateLevel() {
		for (int y = 0; y < levelImageHeight; y++) {
			for (int x = 0; x < levelImageWidth; x++) {
				if (x * y % 10 < 7) {
					tiles[x + y * levelImageWidth] = Tile.GRASS.getId();
				} else {
					tiles[x + y * levelImageWidth] = Tile.STONE.getId();
				}
			}
		}
	}

	public void tick() {
		player.tick();

		for (Tile t : Tile.tiles) {
			if (t == null) {
				break;
			}
			t.tick();
		}
	}

	public void renderTiles(Screen screen) {
		for (int tileY = screen.getTopY()
				/ levelScaleFactor; tileY < screen.getBottomY() / levelScaleFactor
						+ 1; tileY++) {
			for (int tileX = (screen.getLeftX() / levelScaleFactor); tileX < screen
					.getRightX() / levelScaleFactor + 1; tileX++) {
				getTile(tileX, tileY).render(screen, this, tileX * levelScaleFactor,
						tileY * levelScaleFactor, scaleFactor);
			}
		}
	}

	public void renderEntities(Screen screen) {
		player.render(screen);
	}

	public Tile getTile(int x, int y) {
		if (0 > x || x >= levelImageWidth || 0 > y || y >= levelImageHeight)
			return Tile.VOID;
		return Tile.tiles[tiles[x + y * levelImageWidth]];
	}

	public void addPlayer(Player player) {
		this.player = player;
	}

	/***
	 * Return size of level image. NOTE: this is NOT the width of the level
	 * itself. Only the level's source image.
	 * 
	 * @return
	 */
	public int getLevelImageWidth() {
		return levelImageWidth;
	}

	/***
	 * Return size of level image. NOTE: this is NOT the height of the level
	 * itself. Only the level's source image.
	 * 
	 * @return
	 */
	public int getLevelImageHeight() {
		return levelImageHeight;
	}

	/***
	 * Return pixel width of the level in world coordinates. Note: this is
	 * different from the size of the level *image* which is scaled up by a factor
	 * of levelScaleFactor to create the level
	 * 
	 * @return
	 */
	public int getLevelWidth() {
		return getLevelImageWidth() * this.levelScaleFactor;
	}

	/***
	 * Return pixel height of the level in world coordinates. Note: this is
	 * different from the size of the level *image* which is scaled up by a factor
	 * of levelScaleFactor to create the level
	 * 
	 * @return
	 */
	public int getLevelHeight() {
		return getLevelImageWidth() * this.levelScaleFactor;
	}
}
