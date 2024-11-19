package ferpaks.cinema.initializer;

import ferpaks.cinema.entity.StreetType;
import ferpaks.cinema.service.StreetTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StreetTypeInitializer implements ApplicationRunner {
	private final StreetTypeService streetTypeService;

	@Override
	public void run(ApplicationArguments args) {
		if (streetTypeService.findAll().isEmpty()) {
			streetTypeService.save(new StreetType(null, "ул.", "улица"));
			streetTypeService.save(new StreetType(null, "пр.", "проспект"));
			streetTypeService.save(new StreetType(null, "пер.", "переулок"));
			streetTypeService.save(new StreetType(null, "б-р", "бульвар"));
			System.out.println("Инициализация типов улиц завершена.");
		}
	}
}