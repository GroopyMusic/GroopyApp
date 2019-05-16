package adri.suys.un_mutescan.presenter;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import adri.suys.un_mutescan.model.Counterpart;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;
import adri.suys.un_mutescan.viewinterfaces.EventListViewInterface;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class EventPresenterTest {

    @Mock EventListViewInterface viewInterface;
    EventPresenter presenter;
    private JSONArray jsonArrayOK;
    private JSONArray jsonArrayKO;

    @Before
    public void setUp() throws Exception {
        String responseOK = getResponseFromAPI();
        String responseKO = "[\n" +
                "    {\n" +
                "        \"error\": \"Vous n'avez pas d'événements.\"\n" +
                "    }\n" +
                "]";
        jsonArrayOK = new JSONArray(responseOK);
        jsonArrayKO = new JSONArray(responseKO);
        presenter = new EventPresenter(viewInterface);
    }

    @Test
    public void handleJSONArrayTestSuccess(){
        presenter.handleJSONArray(jsonArrayOK);
        List<Event> events = UnMuteDataHolder.getEvents();
        assertEquals(events.get(0).getId(),750051);
        assertEquals(events.get(1).getId(),750048);
        assertEquals(events.get(2).getId(),750041);
        Event e1 = events.get(0);
        assertEquals(e1.getAudience().get(e1.getAudience().size() - 1).getName(), "ynns pakis");
        Event e2 = events.get(1);
        assertEquals(e2.getCounterparts().get(0).getName(), "Ticket général");
        Event e3 = events.get(2);
        Counterpart cp = e3.getCounterparts().get(0);
        int nbSoldTicketForFirstCp = e3.getStatsPerCp().get(cp);
        assertEquals(nbSoldTicketForFirstCp, 1);
    }

    @Test
    public void handleJSONArrayTestFail(){
        presenter.handleJSONArray(jsonArrayKO);
        assertTrue(presenter.getError() != "");
    }

    private String getResponseFromAPI() {
        return "[\n" +
                "    {\n" +
                "        \"id\": 750041,\n" +
                "        \"name\": \"[Ticked-it] Event B\",\n" +
                "        \"nbTotalTicket\": 550,\n" +
                "        \"nbScannedTicket\": 0,\n" +
                "        \"nbSoldTicket\": 4,\n" +
                "        \"nbBoughtOnSiteTicket\": 0,\n" +
                "        \"date\": {\n" +
                "            \"date\": \"2019-05-06 20:00:00.000000\",\n" +
                "            \"timezone_type\": 3,\n" +
                "            \"timezone\": \"Europe/Brussels\"\n" +
                "        },\n" +
                "        \"nbTicketBoughtInCash\": 0,\n" +
                "        \"error\": \"\",\n" +
                "        \"audience\": [],\n" +
                "        \"counterparts\": [\n" +
                "            {\n" +
                "                \"id\": 78,\n" +
                "                \"name\": \"Ticket Fosse\",\n" +
                "                \"price\": 30\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 79,\n" +
                "                \"name\": \"Ticket Assis Adulte\",\n" +
                "                \"price\": 35\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 80,\n" +
                "                \"name\": \"Ticket VIP\",\n" +
                "                \"price\": 50\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 82,\n" +
                "                \"name\": \"Ticket Assis Enfant\",\n" +
                "                \"price\": 30\n" +
                "            }\n" +
                "        ],\n" +
                "        \"detailsTixPerCp\": [\n" +
                "            {\n" +
                "                \"78\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"79\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"80\": 1\n" +
                "            },\n" +
                "            {\n" +
                "                \"82\": 1\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 750051,\n" +
                "        \"name\": \"[Ticked-it] Réunion Festival 14 juin\",\n" +
                "        \"nbTotalTicket\": 50,\n" +
                "        \"nbScannedTicket\": 6,\n" +
                "        \"nbSoldTicket\": 14,\n" +
                "        \"nbBoughtOnSiteTicket\": 24,\n" +
                "        \"date\": {\n" +
                "            \"date\": \"2019-05-15 11:07:35.393933\",\n" +
                "            \"timezone_type\": 3,\n" +
                "            \"timezone\": \"Europe/Brussels\"\n" +
                "        },\n" +
                "        \"nbTicketBoughtInCash\": 24,\n" +
                "        \"error\": \"\",\n" +
                "        \"audience\": [\n" +
                "            {\n" +
                "                \"buyer\": \"Michele Martin\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9835cc6e03d651421\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"Jean Petit\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9845cc6eb34094d61\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"emile louis\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9855cc6efcd814581\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"Bob Eponge\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9865cc6f1f52a9121\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"Jean Paul\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9875cc6fa25a078e1\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"Marie Jesus\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9885cc6fbe1981461\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"faux\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"Jennyfer Jarrow\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9895cc6fe779babf1\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"faux\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"Pierre Blackman\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9905cc701620453c1\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"faux\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"jon doe\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9915cc702b22d4d31\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"faux\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"ynns pakis\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9925cc70391add1e1\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"faux\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"jimmy smith\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9935cc7057bdb5821\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"anonyme\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"N/A\",\n" +
                "                \"barcode\": \"0\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"vrai\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"jimmy smith\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9935cc7057bdb5821\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"faux\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"jimmy smith\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9935cc7057bdb5821\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"faux\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"buyer\": \"jimmy smith\",\n" +
                "                \"ticket_type\": \"Jeton de présence\",\n" +
                "                \"seat_type\": \"Placement libre\",\n" +
                "                \"barcode\": \"cf9935cc7057bdb5821\",\n" +
                "                \"error\": \"\",\n" +
                "                \"is_validated\": \"faux\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"counterparts\": [\n" +
                "            {\n" +
                "                \"id\": 86,\n" +
                "                \"name\": \"Jeton de présence\",\n" +
                "                \"price\": 5\n" +
                "            }\n" +
                "        ],\n" +
                "        \"detailsTixPerCp\": [\n" +
                "            {\n" +
                "                \"86\": 38\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 750048,\n" +
                "        \"name\": \"[Ticked-it] Event C\",\n" +
                "        \"nbTotalTicket\": 400,\n" +
                "        \"nbScannedTicket\": 0,\n" +
                "        \"nbSoldTicket\": 0,\n" +
                "        \"nbBoughtOnSiteTicket\": 0,\n" +
                "        \"date\": {\n" +
                "            \"date\": \"2019-05-24 20:30:00.000000\",\n" +
                "            \"timezone_type\": 3,\n" +
                "            \"timezone\": \"Europe/Brussels\"\n" +
                "        },\n" +
                "        \"nbTicketBoughtInCash\": 0,\n" +
                "        \"error\": \"\",\n" +
                "        \"audience\": [],\n" +
                "        \"counterparts\": [\n" +
                "            {\n" +
                "                \"id\": 84,\n" +
                "                \"name\": \"Ticket général\",\n" +
                "                \"price\": 5\n" +
                "            }\n" +
                "        ],\n" +
                "        \"detailsTixPerCp\": [\n" +
                "            {\n" +
                "                \"84\": 0\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";
    }
}