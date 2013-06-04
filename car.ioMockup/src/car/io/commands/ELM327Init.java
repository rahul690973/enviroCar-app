package car.io.commands;

import java.io.IOException;
import java.io.InputStream;

import car.io.commands.CommonCommand;

/**
 * This method will reset the OBD connection.
 */
public class ELM327Init extends CommonCommand {

	public ELM327Init() {
		super("AXWRI \"ELM327 v1.3a\"");
	}

	/**
	 * Reset command returns an empty string, so we must override the following
	 * two methods.
	 * 
	 * @throws IOException
	 */
	@Override
	public void readResult(InputStream in) throws IOException {
		return;
	}

	@Override
	public String getRawData() {
		return "";
	}

	@Override
	public String getResult() {
		return getRawData();
	}

	@Override
	public String getCommandName() {
		return "ELM327 Init";
	}

}