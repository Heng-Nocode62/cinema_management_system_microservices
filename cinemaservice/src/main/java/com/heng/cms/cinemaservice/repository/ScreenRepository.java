package com.heng.cms.cinemaservice.repository;


import com.heng.cms.cinemaservice.domain.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScreenRepository  extends JpaRepository<Screen, UUID> {

}
