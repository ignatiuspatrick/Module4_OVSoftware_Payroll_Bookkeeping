package nl.utwente.di.OVSoftware;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class TestResource {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String test(@Context HttpServletRequest r) {
		return "" + Login.Security(r.getSession());
	}

}
