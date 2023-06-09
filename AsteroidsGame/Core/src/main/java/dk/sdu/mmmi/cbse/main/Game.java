package dk.sdu.mmmi.cbse.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.beans.EntityProcessingService;
import dk.sdu.mmmi.cbse.beans.GamePluginService;
import dk.sdu.mmmi.cbse.beans.PostEntityProcessingService;
import dk.sdu.mmmi.cbse.managers.GameInputProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Game implements ApplicationListener {

	private static OrthographicCamera cam;
	private final GameData gameData = new GameData();
	private ShapeRenderer sr;
	private World world = new World();

	private AnnotationConfigApplicationContext applicationContext;

	public Game() {
		this.applicationContext = new AnnotationConfigApplicationContext();
		this.applicationContext.scan("dk.sdu.mmmi.cbse.beans");
		this.applicationContext.refresh();
	}

	@Override
	public void create() {

		gameData.setDisplayWidth(Gdx.graphics.getWidth());
		gameData.setDisplayHeight(Gdx.graphics.getHeight());

		cam = new OrthographicCamera(gameData.getDisplayWidth(), gameData.getDisplayHeight());
		cam.translate(gameData.getDisplayWidth() / 2f, gameData.getDisplayHeight() / 2f);
		cam.update();

		sr = new ShapeRenderer();

		Gdx.input.setInputProcessor(
				new GameInputProcessor(gameData)
		);

		((GamePluginService) applicationContext.getBean("gamePluginService")).start(gameData, world);


	}

	@Override
	public void render() {

		// clear screen to black
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gameData.setDelta(Gdx.graphics.getDeltaTime());

		update();

		draw();

		gameData.getKeys().update();
	}

	private void update() {
		// Update
		((EntityProcessingService) applicationContext.getBean("entityProcessingService")).process(gameData, world);

		((PostEntityProcessingService) applicationContext.getBean("postEntityProcessingService")).process(gameData, world);

	}

	private void draw() {
		for (Entity entity : world.getEntities()) {

			sr.begin(ShapeRenderer.ShapeType.Line);

			float[] shapex = entity.getShapeX();
			float[] shapey = entity.getShapeY();

			for (int i = 0, j = shapex.length - 1;
				 i < shapex.length;
				 j = i++) {

				sr.line(shapex[i], shapey[i], shapex[j], shapey[j]);
			}

			sr.end();
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}
