package in.fincase.service;

import java.util.ArrayList;

import in.fincase.dto.MashitlaResponseDto;

public interface IpoAllotmentService {

	public ArrayList<MashitlaResponseDto>  checkIpoAllotment(String ipoName);

}
