package in.fincase.service;

import java.util.List;

import org.springframework.stereotype.Service;

import in.fincase.dto.LinkinTimeDTO;

@Service
public interface LinkiTimeService {

	List<LinkinTimeDTO> getIpoStatus(String ipoName);
}
