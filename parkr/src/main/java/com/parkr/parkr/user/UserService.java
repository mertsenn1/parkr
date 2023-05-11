package com.parkr.parkr.user;

import com.parkr.parkr.auth.AuthenticationRequest;
import com.parkr.parkr.auth.AuthenticationResponse;
import com.parkr.parkr.car.Car;
import com.parkr.parkr.car.CarDto;
import com.parkr.parkr.car.ICarService;
import com.parkr.parkr.common.LocationModel;
import com.parkr.parkr.common.ParkingInfoModel;
import com.parkr.parkr.common.ParkingLotDetailModel;
import com.parkr.parkr.common.ParkingLotModel;
import com.parkr.parkr.common.RecentParkingLotModel;
import com.parkr.parkr.config.JwtService;
import com.parkr.parkr.lot_summary.LotSummary;
import com.parkr.parkr.lot_summary.LotSummaryRepository;
import com.parkr.parkr.parking_lot.IParkingLotService;
import com.parkr.parkr.parking_lot.ParkingLot;
import com.parkr.parkr.parking_lot.ParkingLotRepository;
import com.parkr.parkr.token.Token;
import com.parkr.parkr.token.TokenRepository;
import com.parkr.parkr.token.TokenType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService
{
    private final UserRepository userRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final LotSummaryRepository lotSummaryRepository;
    private final TokenRepository tokenRepository;
    private final ICarService carService;
    private final IParkingLotService parkingLotService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserDto getUserById(Long id)
    {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) return null;

        log.info("User with the id: {} is requested", id);

        return convertToUserDto(user.get());
    }

    
    @Override
    public List<UserDto> getAllCustomers()
    {
        return null;
        /* 
        List<UserDto> customers = userRepository.findAllByIsOwnerIsFalse().stream().map(this::convertToUserDto).toList();
        log.info("All customers are requested with the size: {}", customers.size());
        return customers;
        */
    }
    

    /* */
    @Override
    public List<UserDto> getAllOwners()
    {
        return null;
        /* 
        List<UserDto> owners = userRepository.findAllByIsOwnerIsTrue().stream().map(this::convertToUserDto).toList();
        log.info("All owners are requested with the size: {}", owners.size());
        return owners;
        */
    }

    @Override
    public AuthenticationResponse signUp(UserDto userDto)
    {
        //User user;
        AuthenticationResponse response;
        try
        {

            //user = userRepository.save(convertToUser(userDto, address));

            response = register(userDto);

            /* 
            if (carDtos != null) {
                carDtos.forEach(carDto -> {
                    carService.saveCar(carDto, user.getId());
                });
            }
            */
            
            log.info("User {} is saved with mail: {}", userDto.getName(), userDto.getMail());
        }
        catch (Exception ex)
        {
            log.info("Error occurred while saving the user: {} with mail: {} error: {}", userDto.getName(), userDto.getMail(), ex.getMessage());
            return null;
        }
        System.out.println("Token response: " + response);
        return response;
    }

    @Override
    public List<ParkingInfoModel> getCurrentParkingData() {
        
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(currentUser.getId());

        List<LotSummary> summaryList = lotSummaryRepository.getCurrentLotSummariesOfUser(currentUser.getId());

        ArrayList<ParkingInfoModel> responseList = new ArrayList<>();
        summaryList.forEach(summary -> {
            ParkingInfoModel responseModel = new ParkingInfoModel();
            responseModel.setId(summary.getId());
            responseModel.setName(summary.getParkingLot().getName());
            responseModel.setFee(summary.getFee());
            responseModel.setStartTime(summary.getStartTime());

            responseList.add(responseModel);
        });
        return responseList;
    }

    @Override
    public List<ParkingInfoModel> getPastParkingData() {
        
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(currentUser.getId());

        List<LotSummary> summaryList = lotSummaryRepository.getPastLotSummariesOfUser(currentUser.getId());

        ArrayList<ParkingInfoModel> responseList = new ArrayList<>();
        summaryList.forEach(summary -> {
            ParkingInfoModel responseModel = new ParkingInfoModel();
            responseModel.setId(summary.getId());
            responseModel.setName(summary.getParkingLot().getName());
            responseModel.setFee(null);
            responseModel.setStartTime(summary.getStartTime());

            responseList.add(responseModel);
        });
        return responseList;
    }

    @Override
    public List<RecentParkingLotModel> getRecentParkingData() {
        
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(currentUser.getId());

        List<LotSummary> summaryList = lotSummaryRepository.getRecentLotSummaries(currentUser.getId());

        // lot summaries => parking lot id'ler => place_id'den al.
        ArrayList<RecentParkingLotModel> responseList = new ArrayList<>();
        summaryList.forEach(summary -> {
            ParkingLot parkingLot = parkingLotRepository.findById(summary.getParkingLot().getId()).get();

            ParkingLotDetailModel parkingLotDetail = parkingLotService.getParkingLotByPlaceID(parkingLot.getPlaceId());
            RecentParkingLotModel responseModel = new RecentParkingLotModel();
            // for every summary => get place_id from parking lot id => request to google... get data for each place.

            responseModel.setName(parkingLotDetail.getName());
            responseModel.setDistance(2.5);
            responseModel.setRating(parkingLotDetail.getRating());
            responseModel.setPlaceID(parkingLotDetail.getPlaceID());
            responseModel.setCoordinates(parkingLotDetail.getCoordinates());

            responseList.add(responseModel);
        });

        return responseList;
    }

    private AuthenticationResponse register(UserDto request){
        User user = new User();
        user.setMail(request.getMail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setTokenType(TokenType.BEARER);
        User savedUser = userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
    }

    @Override
    public AuthenticationResponse signIn(AuthenticationRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getMail(), request.getPassword()));
        User user = userRepository.findByMail(request.getMail()).get();
        String jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
        /* 
        User user = userRepository.findByMailAndPassword(mail, password);

        if (user == null) return null;

        log.info("User {} is logged in with mail: {}", user.getName(), user.getMail());
        return convertToUserDto(user);
        */
    }

    @Override
    public void deleteUser(Long id){
        Optional<User> lotSummary = userRepository.findById(id);
        if (!lotSummary.isPresent()) throw new UserNotFoundException("User couldn't found by id: " + id);
        try{
            userRepository.delete(lotSummary.get());
            log.info("User with id is deleted: {}", id);
        }
        catch (Exception ex){
            log.info("Error occurred while deleting the user, error: {}", ex.getMessage());
        }
    }

    private UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .mail(user.getMail())
                .name(user.getName())
                .phone(user.getPhone())
                .cars(convertToCarDtos(userRepository.findCarsOfUser(user.getId()))) // to display cars
                .role(user.getRole())
                .build();
    }

    private List<CarDto> convertToCarDtos(List<Car> cars) {
        if (cars == null || cars.isEmpty()) {
            return new ArrayList<>();
        }

        List<CarDto> carDtos = new ArrayList<>();

        cars.forEach(car -> {
            CarDto carDto = carService.convertToCarDto(car);
            carDtos.add(carDto);
        });
        
        return carDtos;
    }
    
    private User convertToUser(UserDto userDto) {
        return new User(null, userDto.getMail(), userDto.getName(),
                userDto.getPassword(), userDto.getPhone(),
                null, null, userDto.getRole());
    }

    private void saveUserToken(User savedUser, String jwtToken) {
        Token token = Token.builder()
            .user(savedUser)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        validUserTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

}
