package lk.bsc212.pdsa.room;

import static com.google.common.truth.Truth.assertThat;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lk.bsc212.pdsa.model.WeightedGraph;
import lk.bsc212.pdsa.model.room.QueenPlace;
import lk.bsc212.pdsa.room.dao.MinimumConnectorDao;
import lk.bsc212.pdsa.room.dao.QueenPlaceDao;
import lk.bsc212.pdsa.room.dao.ShortestPathDao;
import lk.bsc212.pdsa.utils.PrimsAlgorithm;
import lk.bsc212.pdsa.utils.Queens;

@RunWith(AndroidJUnit4.class)
public class AppDatabaseTest extends TestCase {

    public AppDatabase appDatabase;
    public QueenPlaceDao queenPlaceDao;
    public ShortestPathDao shortestPathDao;
    public MinimumConnectorDao minimumConnectorDao;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        appDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase.class).allowMainThreadQueries().build();

        queenPlaceDao = appDatabase.queenPlaceDao();
        shortestPathDao = appDatabase.shortestPathDao();
        minimumConnectorDao = appDatabase.minimumConnectorDao();
    }

    @After
    public void closeDb() {
        appDatabase.close();
    }

    @Test
    public void writeAndReadQueenPlaces() {


        QueenPlace queenPlace = new QueenPlace(1, "1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0", 12);
        queenPlaceDao.insertAll(queenPlace);
        List<QueenPlace> queenPlaces = queenPlaceDao.getQueenPlaces();
        assertThat(queenPlaces.stream().anyMatch(o -> o.places.equals(queenPlace.places) && o.placeId == queenPlace.placeId && o.answeredUserId == queenPlace.answeredUserId)).isTrue();

    }


    @Test
    public void checkAllPossibleSolutionsAreGenerated() {

        Queens.enumerate(8).forEach(place -> queenPlaceDao.insertAll(new QueenPlace(place)));

        assertThat(queenPlaceDao.combinationCount()).isEqualTo(92);

    }

    @Test
    public void checkStartNodeNotVisitedAgain() {

        int[] answerFromCities = new int[9];
        int[] answerToCities = new int[9];
        int[] answerDistance = new int[9];
        int systemSelectedCity = (int) (Math.random() * (9 + 1) + 0);

        WeightedGraph weightedGraph = new WeightedGraph(10);
        new PrimsAlgorithm().primMST(weightedGraph.getEdges(),
                systemSelectedCity, answerFromCities, answerToCities, answerDistance);
        assertThat(Arrays.stream(answerToCities).anyMatch(i -> i == systemSelectedCity)).isFalse();

    }

    @Test
    public void checkWhetherGraphUnDirectedOrNot() {

        WeightedGraph weightedGraph = new WeightedGraph(10);
        int row = (int) (Math.random() * (9 + 1) + 0);
        int col = (int) (Math.random() * (9 + 1) + 0);

        assertThat(weightedGraph.getEdges()[row][col]).isEqualTo(weightedGraph.getEdges()[col][row]);


    }

}